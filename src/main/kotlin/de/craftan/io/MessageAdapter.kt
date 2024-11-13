package de.craftan.io

import de.craftan.PluginManager
import de.craftan.bridge.util.resolveMiniMessage
import de.craftan.bridge.util.toComponent
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files

/**
 * Util class to help load messages from a configuration file, based on localization
 */
object MessageAdapter {
    private const val LANGUAGE_FILE_LOCATION = "/language/"

    private const val LOCALE_LOCATION = "locale"

    private const val DEFAULT_LOCALE = "en_US"
    private lateinit var defaultConfiguration: FileConfiguration

    private val localeConfigs: MutableMap<String, FileConfiguration> = mutableMapOf()

    /**
     * Loads all required localization files for craftan based on the entries in
     * @see CraftanNotification
     * If no file is provided, the default values from the notifications will be used instead.
     * The files will be auto updated based on the missing keys.
     *
     * Will load every locale file found in the language folder
     * @see LANGUAGE_FILE_LOCATION
     */
    fun load() {
        loadDefault()
        loadLocales()
        updatesLocalesFiles()
    }

    /**
     * @return the found files in the langauges folder [LANGUAGE_FILE_LOCATION]
     */
    fun getResolvedLocales(): List<String> = localeConfigs.keys.toMutableList().apply { add(DEFAULT_LOCALE) }

    /**
     * Creates a new localization file for the given locale.
     * Will copy the current default locale file, to the new locale
     * @param locale to create
     */
    fun createLocaleFromDefault(locale: String) {
        val location = File(PluginManager.dataFolder, "$LANGUAGE_FILE_LOCATION/$locale.yml")
        val defaultLocation = File(PluginManager.dataFolder, "$LANGUAGE_FILE_LOCATION/$DEFAULT_LOCALE.yml")

        if (!defaultLocation.exists()) {
            error("Default localization file not found!")
        }

        if (!location.exists()) {
            Files.copy(defaultLocation.toPath(), location.toPath())
        }
    }

    /**
     * Resolves the given location from the config from the given locale.
     * If the toMiniMessage is true, it will resolve the string using MiniMessage
     * @see DEFAULT_LOCALE
     * @param configLocation location of the string
     * @param locale the locale string
     * @param toMiniMessage whether to resolve the string with mini message
     * @return the given string in the config, or "N/A Message not found"
     */
    fun resolveMessage(
        configLocation: String,
        locale: String?,
        toMiniMessage: Boolean = true,
    ): Component = if (toMiniMessage) resolveRawMessage(configLocation, locale).resolveMiniMessage() else resolveRawMessage(configLocation, locale).toComponent()

    /**
     * Resolves the given location from the config from the given locale.
     * If the locale is not found, it will default to the default locale
     * @see DEFAULT_LOCALE
     * @param configLocation location of the string
     * @param locale the locale string
     * @return the given string in the config, or "N/A Message not found"
     */
    fun resolveRawMessage(
        configLocation: String,
        locale: String?,
    ): String {
        if (locale == null) {
            return defaultConfiguration.getString(configLocation) ?: "N/A Message not found"
        }

        val localeConfig = localeConfigs[locale] ?: return resolveRawMessage(configLocation, null)
        val resolvedMessage = (localeConfig.getString(configLocation) ?: "N/A Message not found")

        return resolvedMessage
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

            localeFile.getKeys(true).forEach { localeKey ->
                if (keys.none { it.notification.configLocation == localeKey || it.notification.configLocation.startsWith(localeKey) }) {
                    localeFile.set(localeKey, null)
                    // TODO add notification that this key has been removed
                }
            }

            val localeFileName = localeFile.get(LOCALE_LOCATION) ?: return
            val file = File(PluginManager.dataFolder, "$LANGUAGE_FILE_LOCATION$localeFileName.yml")
            localeFile.save(file)
        }
    }

    private fun loadDefault() {
        val location = File(PluginManager.dataFolder, "$LANGUAGE_FILE_LOCATION$DEFAULT_LOCALE.yml")

        if (!location.exists()) {
            location.parentFile.mkdirs()
            location.createNewFile()

            defaultConfiguration = YamlConfiguration.loadConfiguration(location)

            defaultConfiguration.set(LOCALE_LOCATION, DEFAULT_LOCALE)

            CraftanNotification.entries.forEach {
                defaultConfiguration.set(it.notification.configLocation, it.notification.default)
            }

            defaultConfiguration.save(location)
        } else {
            defaultConfiguration = YamlConfiguration.loadConfiguration(location)

            if (!validateConfig(defaultConfiguration)) {
                val error = findConfigError(defaultConfiguration)
                error("Error while validating config for locale $DEFAULT_LOCALE because ${error.configLocation} was not set but is required!")
            }
        }
    }

    private fun validateConfig(configuration: FileConfiguration): Boolean {
        if (!configuration.contains(LOCALE_LOCATION)) return false
        return !CraftanNotification.entries.any { !configuration.contains(it.notification.configLocation) }
    }

    private fun findConfigError(configuration: FileConfiguration): MessageNotification {
        if (!configuration.contains(LOCALE_LOCATION)) return MessageNotification(LOCALE_LOCATION, "")
        return CraftanNotification.entries.first { !configuration.contains(it.notification.configLocation) }.notification
    }

    private fun loadLocales() {
        val messagesFolder = File(PluginManager.dataFolder, LANGUAGE_FILE_LOCATION)
        val localeFiles = messagesFolder.listFiles()?.toList()?.filter { it.extension == "yml" } ?: listOf()

        for (localeFile in localeFiles) {
            if (localeFile.name == "$DEFAULT_LOCALE.yml") {
                println("Skipping file $DEFAULT_LOCALE.yml")
                continue
            }

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
