package de.craftan.config

import de.craftan.Craftan
import de.craftan.io.MessageAdapter
import de.craftan.io.config.ConfigFile
import de.craftan.io.config.Configs
import de.craftan.io.config.CraftanConfig as ConfigBase
import de.craftan.util.CraftanSystem
import kotlin.reflect.KClass

object ConfigSystem : CraftanSystem {

    val configs = mutableListOf<ConfigFile<out Any>>()

    private fun <T : Any> addConfig(clazz: KClass<T>): () -> T {
        @Suppress("UNCHECKED_CAST")
        val handle = Configs.of(clazz as KClass<out ConfigBase>) as ConfigFile<T>
        configs += handle
        handle.get()
        return { handle.cachedOrNull() ?: throw IllegalStateException("Failed to load config ${clazz.simpleName}") }
    }

    /**
     * Loads all registered config files and updates them
     * @see de.craftan.io.config.YamlConfigAdapter
     */
    override fun load() {
        Craftan.logger.info("[ConfigSystem] Initializing v2 configurations")
        val craftanConfig = addConfig(CraftanConfig::class)
        val craftanGameConfig = addConfig(CraftanGameConfig::class)
        val databaseConfig = addConfig(DatabaseConfig::class)

        Craftan.configs = CraftanConfigs(craftanConfig, craftanGameConfig, databaseConfig)

        // Initialize language system via MessageAdapter
        Craftan.logger.info("[ConfigSystem] Loading MessageAdapter (languages)")
        MessageAdapter.load()
    }

    fun reload() {
        configs.forEach { it.get() }
    }
}
