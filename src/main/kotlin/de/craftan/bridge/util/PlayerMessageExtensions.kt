package de.craftan.bridge.util

import de.craftan.engine.CraftanPlayer
import de.craftan.util.CraftanPermissions
import net.kyori.adventure.text.Component

fun CraftanPlayer.debug(message: String) {
    if (!hasPermission(CraftanPermissions.RECEIVE_DEBUG)) return
    sendMessage("[DEBUG] $message".toComponent())
}

fun String.toComponent(): Component = Component.text(this)
