package de.craftan.io.config

import de.craftan.Craftan
import de.craftan.io.config.annotations.Description
import de.craftan.io.config.annotations.FlattenToRoot
import de.craftan.io.config.annotations.Location
import de.craftan.io.config.annotations.MapKey
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
 * - Map<String, V> (V can be another supported shape). If the property is annotated with [de.craftan.io.config.annotations.FlattenToRoot],
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
    private fun <R : Any> mergeDataClass(prefix: String, defaultInstance: R, configuration: FileConfiguration): R {
        val kClass = defaultInstance::class
        val ctor = kClass.primaryConstructor
            ?: error("YamlConfigAdapter requires a primary constructor for data class ${kClass.qualifiedName}")

        val args = mutableMapOf<KParameter, Any?>()
        val propsByName = kClass.memberProperties.associateBy { it.name }

        for (param in ctor.parameters) {
            val name = param.name ?: continue
            val prop = propsByName[name] ?: continue
            val defaultValue = prop.getter.call(defaultInstance)
            val childPrefix = resolvePropertyPath(
                prefix,
                name,
                prop.findAnnotation<Location>()?.value,
                prop.findAnnotation<FlattenToRoot>() != null,
                prop.findAnnotation<MapKey>()?.value ?: prop.findAnnotation<de.craftan.io.config.annotations.Map>()?.value
            )
            // Schema-driven merge: if this is a Map and defaults are empty, derive entries from the declared value type
            val mergedValue = if (defaultValue is Map<*, *> && defaultValue.isEmpty()) {
                mergeMapWithSchema(childPrefix, prop, configuration)
            } else {
                mergeValue(childPrefix, defaultValue, configuration)
            }
            val validated = validateProperty(
                property = prop,
                value = mergedValue,
                defaultValue = defaultValue,
                yamlPath = childPrefix,
                sourceFile = file,
                ownerName = kClass.simpleName ?: "<anonymous>"
            )
            args[param] = validated
        }

        return ctor.callBy(args)
    }

    @Suppress("UNCHECKED_CAST")
    private fun mergeValue(prefix: String, defaultValue: Any?, configuration: FileConfiguration): Any? {
        if (defaultValue == null) return null

        return when (defaultValue) {
            is String -> if (configuration.contains(prefix)) configuration.getString(prefix) else defaultValue
            is Int -> if (configuration.contains(prefix)) configuration.getInt(prefix) else defaultValue
            is Long -> if (configuration.contains(prefix)) configuration.getLong(prefix) else defaultValue
            is Double -> if (configuration.contains(prefix)) configuration.getDouble(prefix) else defaultValue
            is Boolean -> if (configuration.contains(prefix)) configuration.getBoolean(prefix) else defaultValue
            is Enum<*> -> {
                if (configuration.contains(prefix)) {
                    val rawValue = configuration.getString(prefix)
                    if (rawValue != null) {
                        val constants = defaultValue.javaClass.enumConstants as Array<out Enum<*>>
                        constants.firstOrNull { it.name == rawValue } ?: defaultValue
                    } else defaultValue
                } else defaultValue
            }
            is Map<*, *> -> {
                // Only Map<String, V> supported. Merge YAML section keys dynamically.
                // Special handling for root-prefix (FlattenToRoot): iterate default keys (which may contain dots)
                // to correctly read nested values like "lobby.joined".
                val defaultMap = defaultValue as Map<String, Any?>
                val result = LinkedHashMap<String, Any?>()
                val section = configuration.getConfigurationSection(prefix)
                val sampleDefault: Any? = defaultMap.values.firstOrNull()

                if (prefix.isEmpty()) {
                    // Flatten-to-root map (e.g., LanguageFile.messages): read values by default keys to support dotted paths
                    for ((key, mapDefault) in defaultMap) {
                        val childPrefix = joinPath(prefix, key)
                        result[key] = mergeValue(childPrefix, mapDefault, configuration)
                    }
                } else if (section != null) {
                    for (key in section.getKeys(false)) {
                        val childPrefix = joinPath(prefix, key)
                        val defaultForKey = defaultMap[key] ?: sampleDefault
                        // If we have any default sample (primitive, list, data class, etc.), merge using it to ensure proper typing and validation.
                        result[key] = if (defaultForKey != null) {
                            mergeValue(childPrefix, defaultForKey, configuration)
                        } else {
                            // No sample available (empty defaults). Try to coerce to common primitives for better validator coverage.
                            // Guard against writing ConfigurationSection objects as values later.
                            val raw = when {
                                configuration.isString(childPrefix) -> configuration.getString(childPrefix)
                                configuration.isInt(childPrefix) -> configuration.getInt(childPrefix)
                                configuration.isLong(childPrefix) -> configuration.getLong(childPrefix)
                                configuration.isDouble(childPrefix) -> configuration.getDouble(childPrefix)
                                configuration.isBoolean(childPrefix) -> configuration.getBoolean(childPrefix)
                                else -> configuration.get(childPrefix)
                            }
                            if (raw is org.bukkit.configuration.ConfigurationSection) null else raw
                        }
                    }
                } else {
                    // No YAML section present; fall back to defaults
                    for ((key, mapValue) in defaultMap) {
                        val childPrefix = joinPath(prefix, key)
                        result[key] = mergeValue(childPrefix, mapValue, configuration)
                    }
                }
                result
            }
            is List<*> -> {
                // Support lists of primitives/enums only
                if (configuration.contains(prefix)) {
                    val list = configuration.getList(prefix)
                    if (list != null) {
                        val coerced = coerceList(list, defaultValue)
                        coerced ?: defaultValue
                    } else defaultValue
                } else defaultValue
            }
            else -> {
                // Nested data class
                val valueClass = defaultValue::class
                if (valueClass.isData) mergeDataClass(prefix, defaultValue, configuration) else defaultValue
            }
        }
    }

    /**
     * Attempts to coerce a raw list read from YAML to the type of the default list's element.
     * Only primitives and enums are supported for list elements.
     */
    private fun coerceList(rawList: List<*>, defaultValue: List<*>): List<*>? {
        val sampleElement = defaultValue.firstOrNull() ?: return rawList
        return when (sampleElement) {
            is String -> rawList.filterIsInstance<String>()
            is Int -> rawList.mapNotNull { (it as? Number)?.toInt() }
            is Long -> rawList.mapNotNull { (it as? Number)?.toLong() }
            is Double -> rawList.mapNotNull { (it as? Number)?.toDouble() }
            is Boolean -> rawList.mapNotNull { it as? Boolean }
            is Enum<*> -> rawList.mapNotNull { it as? String } // we keep as strings and will write back as strings
            else -> null
        }
    }

    // -------- Schema-driven helpers --------
    /**
     * Merges a Map<String, V> section using the declared value type V as schema, when no defaults are provided.
     */
    private fun mergeMapWithSchema(prefix: String, prop: kotlin.reflect.KProperty1<out Any, *>, configuration: FileConfiguration): Map<String, Any?> {
        val section = configuration.getConfigurationSection(prefix)
        val result = LinkedHashMap<String, Any?>()
        if (section == null) return result
        val valueSample = createSchemaSampleForMapValue(prop) ?: return result
        for (key in section.getKeys(false)) {
            val childPrefix = joinPath(prefix, key)
            result[key] = mergeValue(childPrefix, valueSample, configuration)
        }
        return result
    }

    /**
     * Finds the declared generic type for the map value and creates a default sample instance.
     */
    private fun createSchemaSampleForMapValue(prop: kotlin.reflect.KProperty1<out Any, *>): Any? {
        val returnType = prop.returnType
        if (returnType.classifier != Map::class || returnType.arguments.size != 2) return null
        val valueType = returnType.arguments[1].type ?: return null
        return createDefaultForKType(valueType)
    }

    /**
     * Creates a default instance for a given KType, supporting primitives, enums, and data classes.
     */
    private fun createDefaultForKType(kType: kotlin.reflect.KType): Any? {
        val classifier = kType.classifier
        if (classifier !is kotlin.reflect.KClass<*>) return null
        val kClass = classifier
        return when (kClass) {
            String::class -> ""
            Int::class -> 0
            Long::class -> 0L
            Double::class -> 0.0
            Float::class -> 0.0f
            Boolean::class -> false
            else -> {
                if (kClass.java.isEnum) {
                    val constants = kClass.java.enumConstants
                    if (constants != null && constants.isNotEmpty()) constants[0] else null
                } else if (kClass.isData) {
                    instantiateDataClassDefault(kClass)
                } else null
            }
        }
    }

    /**
     * Attempts to instantiate a data class using default parameters; falls back to best-effort primitives.
     */
    private fun instantiateDataClassDefault(kClass: kotlin.reflect.KClass<*>): Any? {
        val ctor = kClass.primaryConstructor ?: return null
        // Try to call with default arguments (all parameters optional with defaults)
        return try {
            ctor.callBy(emptyMap())
        } catch (_: Throwable) {
            // Fallback: attempt to build args from parameter defaults of common primitives
            val args = mutableMapOf<kotlin.reflect.KParameter, Any?>()
            for (parameter in ctor.parameters) {
                val paramType = parameter.type
                val paramDefault = createDefaultForKType(paramType) ?: continue
                args[parameter] = paramDefault
            }
            runCatching { ctor.callBy(args) }.getOrNull()
        }
    }

    // ------------ Write (instance -> YAML) ------------
    private fun writeDataClass(prefix: String, value: Any, out: FileConfiguration) {
        val kClass = value::class
        val props = kClass.memberProperties
        for (prop in props) {
            val propertyValue = prop.getter.call(value) ?: continue
            val description = prop.findAnnotation<Description>()
            val childPrefix = resolvePropertyPath(
                prefix,
                prop.name,
                prop.findAnnotation<Location>()?.value,
                prop.findAnnotation<FlattenToRoot>() != null,
                prop.findAnnotation<MapKey>()?.value ?: prop.findAnnotation<de.craftan.io.config.annotations.Map>()?.value
            )
            writeValue(childPrefix, propertyValue, out)
            if (description != null && description.value.isNotBlank()) {
                setComment(out, childPrefix, description.value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun writeValue(prefix: String, value: Any?, out: FileConfiguration) {
        if (value == null) return
        when (value) {
            is String, is Int, is Long, is Double, is Boolean -> out.set(prefix, value)
            is Enum<*> -> out.set(prefix, value.name)
            is Map<*, *> -> {
                val stringKeyedMap = value as Map<String, Any?>
                for ((entryKey, entryValue) in stringKeyedMap) {
                    writeValue(joinPath(prefix, entryKey), entryValue, out)
                }
            }
            is List<*> -> {
                val normalized = value.map { if (it is Enum<*>) it.name else it }
                out.set(prefix, normalized)
            }
            else -> {
                val valueClass = value::class
                if (valueClass.isData) writeDataClass(prefix, value, out) else out.set(prefix, value)
            }
        }
    }
}
