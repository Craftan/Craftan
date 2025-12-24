package de.craftan.io.config

import de.craftan.Craftan
import de.craftan.PluginManager
import de.craftan.io.config.annotations.LiveConfigRead
import java.io.File
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * High-level convenience API to load YAML-backed config data classes.
 *
 * Notes about get():
 * - get() always reads from disk and merges with defaults on every call (live values).
 * - Because this implies I/O, callers must explicitly opt in using @OptIn(LiveConfigRead::class).
 * - If you need a reusable handle, prefer Configs.of<T>(). The handle's get() has the same opt-in requirement.
 *
 * Usage examples:
 * - val cfg = Configs.get<CraftanConfig>() // requires @ConfigPath on class
 * - val cfg = Configs.get<CraftanConfig>("config/craftan.yml")
 * - val handle = Configs.of<CraftanConfig>("config/craftan.yml")
 */
object Configs {
    /** Returns a ConfigFile handle using the provided relative path under the plugin data folder. */
    inline fun <reified T : CraftanFileConfig> of(relativePath: String, createBackup: Boolean = true): ConfigFile<T> {
        val file = File(PluginManager.dataFolder, relativePath)
        Craftan.logger.fine("[Configs] Creating handle for ${T::class.simpleName} at ${file.absolutePath}")
        val defaults = instantiateDefaults<T>()
        return ConfigFile.of(file, defaults, createBackup)
    }

    /**
     * Constructs a `ConfigFile` instance for the specified configuration type.
     *
     * This method automatically determines the file's relative path using the `@ConfigPath` annotation
     * on the configuration class and instantiates the default configuration values. It optionally
     * creates a backup of the configuration file during updates.
     *
     * @param createBackup Whether to create a backup of the configuration file during updates. Defaults to `true`.
     * @return A `ConfigFile` instance configured for the specified type.
     */
    inline fun <reified T : CraftanFileConfig> of(createBackup: Boolean = true): ConfigFile<T> {
        val path = getRelativePath<T>()
        val file = File(PluginManager.dataFolder, path)
        Craftan.logger.fine("[Configs] Creating handle for ${T::class.simpleName} via @ConfigPath('$path') at ${file.absolutePath}")
        val defaults = instantiateDefaults<T>()
        return ConfigFile.of(file, defaults, createBackup)
    }

    /** Loads and returns the hydrated config using the provided relative path. Performs disk I/O. */
    @LiveConfigRead
    inline fun <reified T : CraftanFileConfig> get(relativePath: String, createBackup: Boolean = true): T =
        of<T>(relativePath, createBackup).get()

    /**
     * Loads and returns the hydrated config using @ConfigPath on the config class to resolve its file location.
     * The config data class must provide defaults for all constructor parameters. Performs disk I/O.
     */
    @LiveConfigRead
    inline fun <reified T : CraftanFileConfig> get(createBackup: Boolean = true): T {
        val k = T::class
        val path = k.findAnnotation<ConfigPath>()?.value
            ?: error("${k.simpleName} is missing @ConfigPath. Use Configs.get<${k.simpleName}>(relativePath) instead.")
        return get<T>(path, createBackup)
    }

    inline fun <reified T : CraftanFileConfig> getRelativePath(): String {
        val k = T::class
        val path = k.findAnnotation<ConfigPath>()?.value ?: error("${k.simpleName} is missing @ConfigPath.")
        return path
    }

    @PublishedApi
    internal inline fun <reified T : CraftanFileConfig> instantiateDefaults(): T {
        val k = T::class
        // Prefer primary constructor with default arguments
        k.primaryConstructor?.let { ctor ->
            try {
                @Suppress("UNCHECKED_CAST")
                return ctor.callBy(emptyMap())
            } catch (_: IllegalArgumentException) {
                // fallthrough to no-arg
            }
        }

        val noArgCtor = k.constructors.firstOrNull { it.parameters.isEmpty() }
        if (noArgCtor != null) {
            return noArgCtor.call()
        }
        error("Cannot instantiate defaults for ${k.simpleName}. Provide default values for all constructor params or use ConfigFile directly.")
    }
}
