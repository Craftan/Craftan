package de.craftan.config

import de.craftan.Craftan
import de.craftan.config.schema.CraftanConfig
import de.craftan.config.schema.CraftanGameConfig
import de.craftan.io.MessageAdapter
import de.craftan.io.config.ConfigFile
import de.craftan.io.config.Configs
import de.craftan.io.config.CraftanFileConfig
import de.craftan.io.config.annotations.LiveConfigRead
import de.craftan.util.CraftanSystem

object ConfigSystem : CraftanSystem {

    val configs = mutableListOf<ConfigFile<out CraftanFileConfig>>()

    @OptIn(LiveConfigRead::class)
    private inline fun <reified T: CraftanFileConfig> addConfig(): () -> T {
        val handle = Configs.of<T>()
        configs += handle
        handle.get()
        return { handle.cachedOrNull() ?: throw IllegalStateException("Failed to load config ${T::class.simpleName}") }
    }

    @OptIn(LiveConfigRead::class)
    /**
     * Loads all registered config files and updates them
     * @see de.craftan.io.config.YamlConfigAdapter
     */
    override fun load() {
        Craftan.logger.info("[ConfigSystem] Initializing CraftanConfig live provider via Configs")
        val craftanConfig = addConfig<CraftanConfig>()
        val craftanGameConfig = addConfig<CraftanGameConfig>()

        Craftan.configs = CraftanConfigs(craftanConfig, craftanGameConfig)

        // Initialize language system via MessageAdapter
        Craftan.logger.info("[ConfigSystem] Loading MessageAdapter (languages)")
        MessageAdapter.load()
    }

    @OptIn(LiveConfigRead::class)
    fun reload() {
        configs.forEach { it.get() }
    }
}
