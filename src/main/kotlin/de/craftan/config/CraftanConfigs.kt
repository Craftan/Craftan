package de.craftan.config

import de.craftan.Craftan
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

data class CraftanConfigs(
    val craftan: () -> CraftanConfig,
    val gameSettings: () -> CraftanGameConfig,
    val database: () -> DatabaseConfig,
    val scoreboards: () -> ScoreboardConfig
) {
    companion object {
        fun configs(): List<String> {
            return CraftanConfigs::class.memberProperties.map { it.name }
        }

        fun valuesForConfig(config: String, instance: CraftanConfigs = Craftan.configs): List<String> {
            val prop = CraftanConfigs::class.memberProperties.firstOrNull { it.name == config }
                ?: return emptyList()

            val lambda = prop.get(instance) as? () -> Any ?: return emptyList()

            val configInstance = lambda()

            return configInstance::class.memberProperties.map { it.name }
        }

        fun getValueForConfig(config: String, path: String, instance: CraftanConfigs = Craftan.configs): Any? {
            val prop = CraftanConfigs::class.memberProperties
                .firstOrNull { it.name == config } as? KProperty1<CraftanConfigs, () -> Any>
                ?: return null

            val configInstance = prop.get(instance).invoke()
            val pathProp = configInstance::class.memberProperties
                .firstOrNull { it.name == path } as? KProperty1<Any, Any>
                ?: return null

            return pathProp.get(configInstance)
        }
    }
}