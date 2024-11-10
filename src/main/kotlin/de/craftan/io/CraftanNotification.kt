package de.craftan.io

/**
 * A collection of all notifications
 */
enum class CraftanNotification(
    val notification: MessageNotification,
) {
    // BASICS
    PREFIX(MessageNotification("prefix", "<grey>[<#00c9a5>CRAFTAN</#00c9a5>]<reset> ", false)),
    BASE_COLOR(MessageNotification("base_color", "<grey>", false)),
    BASE_HIGHLIGHT(MessageNotification("base_highlight", "<#00c9a5>", false)),

    // EXAMPLES TODO Move to correct section
    JOINED_GAME(MessageNotification("game.joined", "%bc%The player %player% just joined the game!")),
    LEFT_GAME(MessageNotification("game.left", "%bc%The player %player% just left the game!")),

    // CRAFTAN COMMAND
    LOCALES(MessageNotification("cmd.craftan.locales", "%bc%We found the following locales:%bh% %locales%")),
    RELOAD_FILES_START(MessageNotification("cmd.craftan.reload", "%bc%Reloading locale files...")),
    RELOAD_FILES_FINISH(MessageNotification("cmd.craftan.finish", "%bc%Finished loading locale files...")),
}
