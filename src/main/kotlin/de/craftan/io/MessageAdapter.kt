package de.craftan.io

import de.craftan.PluginManager
import de.craftan.bridge.util.resolveMiniMessage
import de.craftan.bridge.util.toComponent
import de.craftan.io.config.LanguageYamlAdapter
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files

/**
 * Util class to help load messages from a configuration file, based on localization
 */
object MessageAdapter : FileAdapter {
    private const val LANGUAGE_FILE_LOCATION = "/language/"

    private const val LOCALE_LOCATION = "locale"

    private const val DEFAULT_LOCALE = "en_US"
    private lateinit var defaultConfiguration: FileConfiguration

    // Keep Bukkit configurations to preserve current resolve behavior
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
    override fun load() {
        // Build default map from CraftanNotification
        val defaultMessages = CraftanNotification.entries.associate { it.notification.configLocation to it.notification.default }

        // Ensure default locale exists and is auto-updated
        val defaultFile = File(PluginManager.dataFolder, "$LANGUAGE_FILE_LOCATION$DEFAULT_LOCALE.yml")
        val defaultLayout = LanguageFile(locale = DEFAULT_LOCALE, messages = defaultMessages)
        LanguageYamlAdapter(defaultFile, defaultLayout).loadAndUpdate()
        defaultConfiguration = YamlConfiguration.loadConfiguration(defaultFile)

        // Load and auto-update other locales found in the language folder
        loadLocales(defaultMessages)
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
            val config = YamlConfiguration.loadConfiguration(location)
            config.set("locale", locale)
            config.save(location)
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

    // Auto-update and reload locales using the generic YAML adapter
    private fun updatesLocalesFiles() { /* no-op: handled by load() */ }

    private fun loadDefault() { /* deprecated by generic adapter */ }

    private fun validateConfig(configuration: FileConfiguration): Boolean {
        // Kept for compatibility but not used anymore
        if (!configuration.contains(LOCALE_LOCATION)) return false
        return !CraftanNotification.entries.any { !configuration.contains(it.notification.configLocation) }
    }

    private fun findConfigError(configuration: FileConfiguration): MessageNotification {
        if (!configuration.contains(LOCALE_LOCATION)) return MessageNotification(LOCALE_LOCATION, "")
        return CraftanNotification.entries.first { !configuration.contains(it.notification.configLocation) }.notification
    }

    private fun loadLocales(defaultMessages: Map<String, String>) {
        localeConfigs.clear()
        val messagesFolder = File(PluginManager.dataFolder, LANGUAGE_FILE_LOCATION)
        val localeFiles = messagesFolder
            .listFiles()
            ?.toList()
            // Only real locale YAMLs:
            // - Must have extension exactly "yml"
            // - Exclude legacy backups like "*.bak-<ts>.yml"
            // - Exclude new backups like "*.yml.backup" (defensive, even though extension wouldn't be yml)
            ?.filter { file ->
                val isYml = file.extension == "yml"
                val isLegacyBackup = file.name.contains(".bak-") && file.name.endsWith(".yml")
                val isNewBackup = file.name.endsWith(".yml.backup")
                isYml && !isLegacyBackup && !isNewBackup
            }
            ?: listOf()

        for (file in localeFiles) {
            if (file.name == "$DEFAULT_LOCALE.yml") continue

            val localeCode = file.nameWithoutExtension
            val layout = LanguageFile(locale = localeCode, messages = defaultMessages)
            // Auto-update with backup and then reload as Bukkit config for resolve functions
            LanguageYamlAdapter(file, layout).loadAndUpdate()

            if (localeConfigs.contains(localeCode)) {
                error("Locale $localeCode is already loaded. Please double check your files!")
            }
            localeConfigs[localeCode] = YamlConfiguration.loadConfiguration(file)
        }
    }
}
