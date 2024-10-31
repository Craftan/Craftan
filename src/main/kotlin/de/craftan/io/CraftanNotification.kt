package de.craftan.io

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
    LEFT_GAME(MessageNotification("game.joined", "The player %player% just left the game!")),
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
    player: Player?,
    placeholders: Map<CraftanPlaceholders, String>,
): String {
    var raw = notification.resolve(player)

    placeholders.forEach { (placeholder, content) -> raw = raw.replace(placeholder.placeholder, content) }
    return raw
}

fun CraftanNotification.resolve(player: Player?): String = notification.resolve(player)

fun MessageNotification.resolve(player: Player?): String {
    var locale: String? = null

    if (player != null) {
        val playerLocale = player.locale()
        locale = playerLocale.displayLanguage
    }

    val configuresMessage = MessageAdapter.resolveMessage(configLocation, locale)

    val prefix = if (usesPrefix) "${PREFIX_STRING.resolve(player)} " else ""

    return "$prefix$configLocation"
}
