package de.craftan.bridge.util

import de.craftan.engine.CraftanPlayer
import de.craftan.util.CraftanPermissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

fun CraftanPlayer.debug(message: String) {
    if (!hasPermission(CraftanPermissions.RECEIVE_DEBUG)) return
    sendMessage("[DEBUG] $message".toComponent())
}

fun String.toComponent(): TextComponent = Component.text(this)
