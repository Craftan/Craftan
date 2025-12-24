package de.craftan.io

import de.craftan.Craftan
import de.craftan.bridge.util.resolveMiniMessage
import de.craftan.bridge.util.toComponent
import de.craftan.config.schema.LanguageFile
import de.craftan.io.config.ConfigFile
import de.craftan.io.config.annotations.LiveConfigRead
import net.kyori.adventure.text.Component

/**
 * Util class to help load messages from a configuration file, based on localization.
 * Adopted to the generic ConfigFile system for live, auto-updating reads.
 */
@OptIn(LiveConfigRead::class)
object MessageAdapter : FileAdapter {
    private const val DEFAULT_LOCALE = "en_US"

    // Live config handles per locale
    private val localeHandles: MutableMap<String, ConfigFile<LanguageFile>> = mutableMapOf()
    private var defaultHandle: ConfigFile<LanguageFile>? = null

    /**
     * Loads/initializes language files and sets up live handles for each locale.
     * Uses CraftanNotification defaults to seed/update files.
     */
    override fun load() {
        Craftan.logger.info("[MessageAdapter] Loading language files (default=$DEFAULT_LOCALE)...")
        val defaultMessages = CraftanNotification.entries.associate { it.notification.configLocation to it.notification.default }
        Craftan.logger.fine("[MessageAdapter] Default messages count: ${defaultMessages.size}")

        // Ensure default locale exists and is auto-updated
        val def = LanguageFiles.handle(DEFAULT_LOCALE, defaultMessages)
        def.get() // write/update if needed
        defaultHandle = def
        Craftan.logger.info("[MessageAdapter] Default locale '$DEFAULT_LOCALE' initialized")

        // Load and auto-update other locales found in the language folder
        loadLocales(defaultMessages)
    }

    /**
     * @return the found files in the languages folder including the default locale
     */
    fun getResolvedLocales(): List<String> = localeHandles.keys.toMutableList().apply { add(DEFAULT_LOCALE) }

    /**
     * Creates a new localization file for the given locale from current defaults.
     */
    fun createLocaleFromDefault(locale: String) {
        Craftan.logger.info("[MessageAdapter] Creating locale '$locale' from defaults")
        val defaultMessages = CraftanNotification.entries.associate { it.notification.configLocation to it.notification.default }
        val handle = LanguageFiles.handle(locale, defaultMessages)
        handle.get() // ensure created and seeded
        localeHandles[locale] = handle
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
        val effectiveLocale = locale ?: DEFAULT_LOCALE
        val handle = if (locale == null) defaultHandle else localeHandles[locale]
        val message = handle?.get()?.messages?.get(configLocation)
        if (message != null) return message
        Craftan.logger.fine("[MessageAdapter] Missing message '$configLocation' in locale '$effectiveLocale', falling back to default")
        // Fallback to default
        val fallback = defaultHandle?.get()?.messages?.get(configLocation)
        if (fallback == null) {
            Craftan.logger.warning("[MessageAdapter] Missing message '$configLocation' in default locale '$DEFAULT_LOCALE'")
        }
        return fallback ?: "N/A Message not found"
    }

    private fun loadLocales(defaultMessages: Map<String, String>) {
        localeHandles.clear()
        // Discover existing locale files (excluding backups and default)
        val localeFiles = LanguageFiles.listLocales()
        Craftan.logger.fine("[MessageAdapter] Found locales: ${localeFiles.joinToString(", ")}")
        for (locale in localeFiles) {
            if (locale == DEFAULT_LOCALE) continue
            val handle = LanguageFiles.handle(locale, defaultMessages)
            handle.get() // update if needed
            if (localeHandles.contains(locale)) {
                error("Locale $locale is already loaded. Please double check your files!")
            }
            localeHandles[locale] = handle
            Craftan.logger.info("[MessageAdapter] Loaded locale '$locale'")
        }
    }
}
