package de.craftan.io

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.entity.Player

/**
 * Resolves the given notification with the given placeholders
 * @param locale localization for resolving the text
 * @param placeholders a map where the placeholder maps to the content
 */
fun CraftanNotification.resolveWithPlaceholder(
    locale: String?,
    placeholders: Map<CraftanPlaceholder, Component>,
): Component {
    var component = notification.resolve(locale)

    placeholders.forEach { (placeholder, content) ->
        val replacer =
            TextReplacementConfig
                .builder()
                .match(placeholder.placeholder)
                .replacement(content)
                .build()
        component = component.replaceText(replacer)
    }

    return component
}

fun CraftanNotification.resolve(player: Player?): Component {
    var locale: String? = null
    if (player != null) {
        locale = player.locale().displayName
    }

    return notification.resolve(locale)
}

/**
 * Resolves this notification with the value which is in the given locale file.
 * If the locale provided has no localization file, it will default to en_US
 * This will also replace the following values:
 * - PREFIX
 * - BASE_COLOR
 * - BASE_HIGHLIGHT
 *
 * if [MessageNotification.usesPrefix] is disabled, then the prefix won't be replaced
 * @param locale locale to resolve the message
 * @return Component with the resolved component, ready to send to the player
 */
fun CraftanNotification.resolve(locale: String?): Component = notification.resolve(locale)

/**
 * Resolves the given notification without replacing values!
 * @see resolve
 * @param locale the locale to use
 * @param resolveMiniMessage whether the given text should be parsed by the MiniMessage parser.
 *  The MiniMessage parser will resolve styling like <red> in the string directly and return the resolved component.
 *  Without MiniMessage the styling will be in plain text, and can be resolved in a later component.
 * @return component without replacements
 */
fun CraftanNotification.resolveWithoutReplacements(
    locale: String?,
    resolveMiniMessage: Boolean = true,
): Component = notification.resolveWithoutReplacements(locale, resolveMiniMessage)

/**
 * Resolves the notification to the plain string provided by the locale
 * @param locale the locale
 */
fun CraftanNotification.resolveRaw(locale: String?): String = notification.resolveRaw(locale)

fun CraftanNotification.resolveWithPlaceholder(
    player: Player?,
    placeholders: Map<CraftanPlaceholder, Component>,
): Component {
    var locale: String? = null
    if (player != null) {
        locale = player.locale().toString()
    }

    return resolveWithPlaceholder(locale, placeholders)
}
