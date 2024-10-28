package de.craftan.util

/**
 * Models a notification string which will be retrieved from the config.
 */
data class CraftanNotification(
    val configLocation: String,
    val default: String,
    val usesPrefix: Boolean = true,
)

val PREFIX_STRING = CraftanNotification("prefix", "[CRAFTAN]", false)

/**
 * A collection of all notifications
 */
enum class CraftanNotifications(
    val notification: CraftanNotification,
) {
    JOINED_GAME(CraftanNotification("game.joined", "The player %player% just joined the game!")),
    LEFT_GAME(CraftanNotification("game.joined", "The player %player% just left the game!")),
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
 */
fun CraftanNotifications.resolveWithPlaceholder(placeholders: Map<CraftanPlaceholders, String>): String {
    var raw = notification.resolve()

    placeholders.forEach { (placeholder, content) -> raw = raw.replace(placeholder.placeholder, content) }
    return raw
}

fun CraftanNotifications.resolve(): String = notification.resolve()

fun CraftanNotification.resolve(): String {
    // TODO: resolve string from config
    val prefix = if (usesPrefix) "${PREFIX_STRING.resolve()} " else ""

    return "$prefix$default"
}
