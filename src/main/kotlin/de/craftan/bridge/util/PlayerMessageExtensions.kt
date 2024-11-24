package de.craftan.bridge.util

import de.craftan.engine.CraftanPlayer
import de.craftan.io.CraftanNotification
import de.craftan.io.resolve
import de.craftan.util.CraftanPermissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

fun CraftanPlayer.debug(message: String) {
    if (!hasPermission(CraftanPermissions.RECEIVE_DEBUG)) return
    sendMessage("[DEBUG] $message".toComponent())
}

/**
 * Sends the given notification resolved by the players locale
 * @param notification to send
 */
fun Player.sendNotification(notification: CraftanNotification) {
    sendMessage(notification.resolve(this))
}

fun String.toComponent(): TextComponent = Component.text(this)

fun String.resolveMiniMessage(): Component = MiniMessage.miniMessage().deserialize(this)
