package de.craftan.io

import de.craftan.PluginManager
import de.craftan.bridge.util.toComponent
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object MessageAdapter {
    private const val messagesLocation = "/language/"

    private const val LOCALE_LOCATION = "locale"

    private const val defaultLocale = "en_US"
    private lateinit var defaultConfiguration: FileConfiguration

    private val localeConfigs: MutableMap<String, FileConfiguration> = mutableMapOf()
    private val localeConfigFiles: MutableMap<String, File> = mutableMapOf()

    fun load() {
        loadDefault()
        loadLocales()
        updatesLocalesFiles()
    }

    fun resolveMessage(
        configLocation: String,
        locale: String?,
    ): Component {
        if (locale == null) {
            return (defaultConfiguration.get(configLocation)?.toString() ?: "N/A Message not found").toComponent()
        }

        val localeConfig = localeConfigs[locale] ?: return resolveMessage(configLocation, null)
        return (localeConfig.get(configLocation)?.toString() ?: "N/A Message not found").toComponent()
    }

    private fun updatesLocalesFiles() {
        val localeFiles = localeConfigs.values.toMutableList()
        localeFiles += defaultConfiguration

        for (localeFile in localeFiles) {
            val keys = CraftanNotification.entries

            keys.forEach {
                if (!localeFile.contains(it.notification.configLocation)) {
                    localeFile.set(it.notification.configLocation, it.notification.default)
                    // TODO add notification that this key has been added
                }
            }

            localeFile.getKeys(false).forEach { localeKey ->
                if (keys.none { it.notification.configLocation == localeKey }) {
                    localeFile.set(localeKey, null)
                    // TODO add notification that this key has been removed
                }
            }

            val localeFileName = localeFile.get(LOCALE_LOCATION)
            val file = File(PluginManager.dataFolder, "$messagesLocation$localeFileName.yml")
            localeFile.save(file)
        }
    }

    private fun loadDefault() {
        val location = File(PluginManager.dataFolder, "$messagesLocation$defaultLocale.yml")

        if (!location.exists()) {
            location.parentFile.mkdirs()
            location.createNewFile()

            defaultConfiguration = YamlConfiguration.loadConfiguration(location)

            defaultConfiguration.set(PREFIX_STRING.configLocation, PREFIX_STRING.default)
            defaultConfiguration.set(LOCALE_LOCATION, defaultLocale)

            CraftanNotification.entries.forEach {
                defaultConfiguration.set(it.notification.configLocation, it.notification.default)
            }

            defaultConfiguration.save(location)
        } else {
            defaultConfiguration = YamlConfiguration.loadConfiguration(location)

            if (!validateConfig(defaultConfiguration)) {
                val error = findConfigError(defaultConfiguration)
                error("Error while validating config for locale $defaultLocale because ${error.configLocation} was not set but is required!")
            }
        }
    }

    private fun validateConfig(configuration: FileConfiguration): Boolean {
        if (!configuration.contains(PREFIX_STRING.configLocation)) return false
        if (!configuration.contains(LOCALE_LOCATION)) return false
        return CraftanNotification.entries.any { !configuration.contains(it.notification.configLocation) }
    }

    private fun findConfigError(configuration: FileConfiguration): MessageNotification {
        if (!configuration.contains(PREFIX_STRING.configLocation)) return PREFIX_STRING
        if (!configuration.contains(LOCALE_LOCATION)) return MessageNotification(LOCALE_LOCATION, "")
        return CraftanNotification.entries.first { !configuration.contains(it.notification.configLocation) }.notification
    }

    private fun loadLocales() {
        val messagesFolder = File(PluginManager.dataFolder, messagesLocation)
        val localeFiles = messagesFolder.listFiles()?.toList() ?: listOf()

        for (localeFile in localeFiles) {
            println(localeFile.name)
            if (localeFile.name == "$defaultLocale.yml") continue // dont load english again

            val locale = localeFile.nameWithoutExtension
            val localeConfig = YamlConfiguration.loadConfiguration(localeFile)

            if (!validateConfig(localeConfig)) {
                val error = findConfigError(localeConfig)
                error("Error while validating config for locale $locale because ${error.configLocation} was not set but is required!")
            }

            if (localeConfigs.contains(locale)) {
                error("Locale $locale is already loaded. Please double check your files!")
            }

            localeConfigs[locale] = localeConfig
        }
    }
}
