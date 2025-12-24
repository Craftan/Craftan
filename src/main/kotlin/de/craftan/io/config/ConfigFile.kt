package de.craftan.io.config

import de.craftan.Craftan
import de.craftan.io.config.annotations.LiveConfigRead
import java.io.File

/**
 * Small helper wrapper around YamlConfigAdapter to make loading configs dead simple.
 *
 * Important: get() performs a full disk read & merge on every call (live values). You must opt in via
 * @OptIn(LiveConfigRead::class) to acknowledge the I/O. Use cachedOrNull() if you need a quick, in-memory check.
 *
 * Usage:
 * val cfg = ConfigFile({ File(dataFolder, "config/craftan.yml") }, { CraftanConfig() }).get()
 *
 * It will ensure folders/files exist, merge with defaults, write back normalized YAML,
 * create a .backup alongside when changes are written, and return the hydrated data class.
 */
class ConfigFile<T : CraftanFileConfig>(
    private val fileProvider: () -> File,
    private val defaultsProvider: () -> T,
    private val createBackup: Boolean = true,
) {
    @Volatile
    private var cached: T? = null

    /**
     * Loads and returns the hydrated config from disk every call. Also caches the instance in-memory.
     */
    @LiveConfigRead
    fun get(): T = synchronized(this) {
        val file = fileProvider()
        Craftan.logger.fine("[ConfigFile] Loading ${file.absolutePath}")
        // Ensure parent directories exist
        file.parentFile?.mkdirs()
        val adapter = object : YamlConfigAdapter<T>(file, defaultsProvider(), createBackup) {}
        val loaded = adapter.loadAndUpdate()
        cached = loaded
        Craftan.logger.fine("[ConfigFile] Loaded ${loaded::class.simpleName} from ${file.name}")
        loaded
    }

    /** Returns the last loaded instance if available, without touching disk. */
    fun cachedOrNull(): T? = cached

    /** Exposes the resolved file location. */
    fun file(): File = fileProvider()

    companion object {
        fun <T : CraftanFileConfig> of(file: File, defaults: T, createBackup: Boolean = true): ConfigFile<T> =
            ConfigFile({ file }, { defaults }, createBackup)
    }
}
