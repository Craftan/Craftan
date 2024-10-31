package de.craftan.io

import de.craftan.bridge.util.toComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player

/**
 * Models a notification string which will be retrieved from the config.
 */
data class MessageNotification(
    val configLocation: String,
    val default: String,
    val usesPrefix: Boolean = true,
)

val PREFIX_STRING = MessageNotification("prefix", "[CRAFTAN]", false)

/**
 * A collection of all notifications
 */
enum class CraftanNotification(
    val notification: MessageNotification,
) {
    JOINED_GAME(MessageNotification("game.joined", "The player %player% just joined the game!")),
    LEFT_GAME(MessageNotification("game.left", "The player %player% just left the game!")),
}

/**
 * Models a placeholder in a craftan notification
 * @see resolveWithPlaceholder
 */
enum class CraftanPlaceholders(
    val placeholder: String,
) {
    PLAYER("%placer%"),
}

/**
 * Resolves the given notification with the given placeholders
 * @param placeholders a map where the placeholder maps to the content
 * @param player to resolve the locale to, null to use default locale
 */
fun CraftanNotification.resolveWithPlaceholder(
    locale: String?,
    placeholders: Map<CraftanPlaceholders, String>,
): TextComponent {
    var raw = notification.resolve(locale).content()

    placeholders.forEach { (placeholder, content) -> raw = raw.replace(placeholder.placeholder, content) }
    return raw.toComponent()
}

fun CraftanNotification.resolve(player: Player?): TextComponent {
    var locale: String? = null
    if (player != null) {
        locale = player.locale().displayName
    }

    return notification.resolve(locale)
}

fun CraftanNotification.resolve(locale: String?): TextComponent = notification.resolve(locale)

fun CraftanNotification.resolveWithPlaceholder(
    player: Player?,
    placeholders: Map<CraftanPlaceholders, String>,
): TextComponent {
    var locale: String? = null
    if (player != null) {
        locale = player.locale().displayName
    }

    return resolveWithPlaceholder(locale, placeholders)
}

fun MessageNotification.resolve(locale: String?): TextComponent {
    val configuredMessage = MessageAdapter.resolveMessage(configLocation, locale)

    val prefix = if (usesPrefix) PREFIX_STRING.resolve(locale) else Component.empty()

    return prefix.append(configuredMessage)
}
