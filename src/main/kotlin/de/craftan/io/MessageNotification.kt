package de.craftan.io

import de.craftan.bridge.util.resolveMiniMessage
import net.kyori.adventure.text.Component

/**
 * Models a notification string which will be retrieved from the config.
 * @param configLocation the location inside the configuration file
 * @param default the default value of this notification
 * @param usesPrefix if this notification should be prepended using the prefix
 * @see CraftanNotification.PREFIX
 */
data class MessageNotification(
    val configLocation: String,
    val default: String,
    val usesPrefix: Boolean = true,
)

/**
 * Resolves the given string, without replacing the default replacements
 * @see resolve
 *
 * @param locale the locale to resolve the string
 * @param resolveMiniMessage whether to resolve the string using mini message
 */
fun MessageNotification.resolveWithoutReplacements(
    locale: String?,
    resolveMiniMessage: Boolean = true,
): Component {
    val configuredMessage = MessageAdapter.resolveMessage(configLocation, locale, resolveMiniMessage)

    val prefix = if (usesPrefix) CraftanNotification.PREFIX.resolve(locale) else Component.empty()
    return prefix.append(configuredMessage)
}

/**
 * Resolves the raw notification by the given locale, without resolving anything in the string
 */
fun MessageNotification.resolveRaw(locale: String?): String = MessageAdapter.resolveRawMessage(configLocation, locale)

/**
 * Resolved this notification with the given locale and builds a finished component for the player
 *
 * Will replace the following:
 * - %bc% - Base Color
 * - %bh% - Base Highlight
 *
 * Will also resolve the prefix if a prefix is required
 */
fun MessageNotification.resolve(locale: String?): Component {
    val configuredMessage = MessageAdapter.resolveRawMessage(configLocation, locale)

    val prefix = if (usesPrefix) CraftanNotification.PREFIX.resolveRaw(locale) else ""
    val baseColor = CraftanNotification.BASE_COLOR.resolveRaw(locale)
    val baseHighlight = CraftanNotification.BASE_HIGHLIGHT.resolveRaw(locale)

    val replaced =
        configuredMessage
            .replace(CraftanPlaceholder.BASE_COLOR.placeholder, baseColor)
            .replace(CraftanPlaceholder.BASE_HIGHLIGHT.placeholder, baseHighlight)

    return (prefix + replaced).resolveMiniMessage()
}
