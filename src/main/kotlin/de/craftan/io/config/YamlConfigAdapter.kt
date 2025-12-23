package de.craftan.io.config

import de.craftan.Craftan
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


/**
 * A small, reflection-based YAML adapter that can:
 * - Take a Kotlin data class instance as the layout (defaults)
 * - Merge existing YAML values (preserve old values for known fields)
 * - Drop unknown keys from the output
 * - Backup the old file before writing an updated one
 *
 * The adapter relies on Bukkit's YamlConfiguration to read/write YAML.
 * Supported shapes in the data class:
 * - Primitives (String, Int, Long, Double, Boolean)
 * - Enums (stored as name)
 * - Nested data classes
 * - Map<String, V> (V can be another supported shape). If the property is annotated with [FlattenToRoot],
 *   map entries are written/read at the root (or current prefix) rather than under the property name.
 * - List of primitives (String, Int, Long, Double, Boolean) and enums. Nested lists of complex objects are not supported.
 */
abstract class YamlConfigAdapter<T : CraftanFileConfig>(
    private val file: File,
    private val defaultLayout: T,
    private val createBackup: Boolean = true,
) {
    /**
     * Loads the given file, merges existing values into the provided default layout instance, and writes back
     * a normalized YAML file while creating a timestamped backup of the original if it existed.
     * Returns the merged instance.
     */
    fun loadAndUpdate(): T {
        Craftan.logger.fine("[YamlConfigAdapter] Loading config: ${file.absolutePath}")
        val existedBefore = file.exists()
        if (!existedBefore) {
            file.parentFile.mkdirs()
            file.createNewFile()
            Craftan.logger.info("[YamlConfigAdapter] Created new config file: ${file.absolutePath}")
        }

        val existing = YamlConfiguration.loadConfiguration(file)
        val merged = mergeDataClass("", defaultLayout, existing)
        Craftan.logger.fine("[YamlConfigAdapter] Merged instance of ${defaultLayout::class.simpleName} from YAML")

        // Prepare the desired normalized output
        val desired = YamlConfiguration()
        writeDataClass("", merged, desired)
        val desiredString = desired.saveToString()

        // Serialize current (as loaded) to detect if anything would change
        val currentString = existing.saveToString()

        // Only perform backup + write if there is an actual change or it's a brand new file
        val shouldWrite = !existedBefore || desiredString != currentString
        Craftan.logger.fine("[YamlConfigAdapter] ${if (shouldWrite) "Changes detected" else "No changes detected"} for ${file.name}")

        if (shouldWrite) {
            if (createBackup && existedBefore) {
                Craftan.logger.fine("[YamlConfigAdapter] Creating backup for ${file.name} -> ${file.name}.backup")
                onBeforeBackup(file)
                createBackupFile(file)
            }

            desired.save(file)
            Craftan.logger.info("[YamlConfigAdapter] Wrote updated config to ${file.absolutePath}")
            onAfterWrite(file, merged)
        }

        return merged
    }

    // Hooks for subclasses
    protected open fun onBeforeBackup(existing: File) {}
    protected open fun onAfterWrite(file: File, merged: T) {}

    /**
     * Creates a backup next to the file using the pattern: `<original>.backup`.
     * Example: `en_US.yml` -> `en_US.yml.backup`.
     *
     * Note: We previously used a timestamped pattern like `name.bak-<ts>.yml`.
     * We now prefer the `.yml.backup` suffix so scanners can easily ignore backups.
     * Backups are only created when a write actually occurs.
     */
    private fun createBackupFile(file: File) {
        val target = File(file.parentFile, file.name + ".backup")
        try {
            Files.copy(file.toPath(), target.toPath())
        } catch (_: Exception) {
            // best effort; ignore backup failures
        }
    }

    // ------------ Merge (read existing -> instance) ------------
    private fun <R : Any> mergeDataClass(prefix: String, default: R, cfg: FileConfiguration): R {
        val kClass = default::class
        val ctor = kClass.primaryConstructor
            ?: error("YamlConfigAdapter requires a primary constructor for data class ${kClass.qualifiedName}")

        val args = mutableMapOf<KParameter, Any?>()
        val propsByName = kClass.memberProperties.associateBy { it.name }

        for (param in ctor.parameters) {
            val name = param.name ?: continue
            val prop = propsByName[name] ?: continue
            val defaultValue = prop.getter.call(default)
            val childPrefix = resolvePropertyPath(
                prefix,
                name,
                prop.findAnnotation<Location>()?.value,
                prop.findAnnotation<Section>()?.value,
                prop.findAnnotation<FlattenToRoot>() != null
            )
            val mergedValue = mergeValue(childPrefix, defaultValue, cfg)
            args[param] = mergedValue
        }

        return ctor.callBy(args)
    }

    @Suppress("UNCHECKED_CAST")
    private fun mergeValue(prefix: String, defaultValue: Any?, cfg: FileConfiguration): Any? {
        if (defaultValue == null) return null

        return when (defaultValue) {
            is String -> if (cfg.contains(prefix)) cfg.getString(prefix) else defaultValue
            is Int -> if (cfg.contains(prefix)) cfg.getInt(prefix) else defaultValue
            is Long -> if (cfg.contains(prefix)) cfg.getLong(prefix) else defaultValue
            is Double -> if (cfg.contains(prefix)) cfg.getDouble(prefix) else defaultValue
            is Boolean -> if (cfg.contains(prefix)) cfg.getBoolean(prefix) else defaultValue
            is Enum<*> -> {
                if (cfg.contains(prefix)) {
                    val raw = cfg.getString(prefix)
                    if (raw != null) {
                        val constants = defaultValue.javaClass.enumConstants as Array<out Enum<*>>
                        constants.firstOrNull { it.name == raw } ?: defaultValue
                    } else defaultValue
                } else defaultValue
            }
            is Map<*, *> -> {
                // Only Map<String, V> supported
                val defMap = defaultValue as Map<String, Any?>
                val result = LinkedHashMap<String, Any?>()
                for ((k, v) in defMap) {
                    val childPrefix = joinPath(prefix, k)
                    result[k] = mergeValue(childPrefix, v, cfg)
                }
                result
            }
            is List<*> -> {
                // Support lists of primitives/enums only
                if (cfg.contains(prefix)) {
                    val list = cfg.getList(prefix)
                    if (list != null) {
                        val coerced = coerceList(list, defaultValue)
                        coerced ?: defaultValue
                    } else defaultValue
                } else defaultValue
            }
            else -> {
                // Nested data class
                val kClass = defaultValue::class
                if (kClass.isData) mergeDataClass(prefix, defaultValue, cfg) else defaultValue
            }
        }
    }

    private fun coerceList(raw: List<*>, defaultValue: List<*>): List<*>? {
        val sample = defaultValue.firstOrNull() ?: return raw
        return when (sample) {
            is String -> raw.filterIsInstance<String>()
            is Int -> raw.mapNotNull { (it as? Number)?.toInt() }
            is Long -> raw.mapNotNull { (it as? Number)?.toLong() }
            is Double -> raw.mapNotNull { (it as? Number)?.toDouble() }
            is Boolean -> raw.mapNotNull { it as? Boolean }
            is Enum<*> -> raw.mapNotNull { it as? String } // we keep as strings and will write back as strings
            else -> null
        }
    }

    // ------------ Write (instance -> YAML) ------------
    private fun writeDataClass(prefix: String, value: Any, out: FileConfiguration) {
        val kClass = value::class
        val props = kClass.memberProperties
        for (prop in props) {
            val v = prop.getter.call(value) ?: continue
            val sectionAnno = prop.findAnnotation<Section>()
            val childPrefix = resolvePropertyPath(
                prefix,
                prop.name,
                prop.findAnnotation<Location>()?.value,
                sectionAnno?.value,
                prop.findAnnotation<FlattenToRoot>() != null
            )
            // If a section with comment is defined, try to attach a comment to that section path.
            if (sectionAnno != null && sectionAnno.comment.isNotBlank()) {
                ensureSectionAndComment(out, joinPath(prefix, normalizeLocation(sectionAnno.value) ?: ""), sectionAnno.comment)
            }
            writeValue(childPrefix, v, out)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun writeValue(prefix: String, v: Any?, out: FileConfiguration) {
        if (v == null) return
        when (v) {
            is String, is Int, is Long, is Double, is Boolean -> out.set(prefix, v)
            is Enum<*> -> out.set(prefix, v.name)
            is Map<*, *> -> {
                val map = v as Map<String, Any?>
                for ((k, mv) in map) {
                    writeValue(joinPath(prefix, k), mv, out)
                }
            }
            is List<*> -> {
                val normalized = v.map { if (it is Enum<*>) it.name else it }
                out.set(prefix, normalized)
            }
            else -> {
                val k = v::class
                if (k.isData) writeDataClass(prefix, v, out) else out.set(prefix, v)
            }
        }
    }
}
