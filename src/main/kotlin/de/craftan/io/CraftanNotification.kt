package de.craftan.io

/**
 * A collection of all notifications
 */
enum class CraftanNotification(
    val notification: MessageNotification,
) {
    // BASICS
    PREFIX(MessageNotification("prefix", "<grey>[<#00c9a5>CRAFTAN</#00c9a5>]<reset>", false)),
    PREFIX_SEPARATOR(MessageNotification("prefix_separator", " » ", false)),
    BASE_COLOR(MessageNotification("base_color", "<grey>", false)),
    BASE_HIGHLIGHT(MessageNotification("base_highlight", "<#00c9a5>", false)),

    // EXAMPLES TODO Move to correct section
    JOINED_GAME(MessageNotification("game.joined", "%bc%The player %player% just joined the game!")),
    LEFT_GAME(MessageNotification("game.left", "%bc%The player %player% just left the game!")),

    // CRAFTAN COMMAND
    INFORMATION_FORMATING(
        MessageNotification(
            "cmd.craftan.info.formating",
            """
            %bc%<st>⎯⎯⎯</st>%prefix%%bc%<st>⎯⎯⎯</st>
            %bc%Usage: /%bh%%command_name% %command_args%
            %bc%Description: %bh%%command_description%
            """.trimIndent(),
            false,
        ),
    ),
    LOCALES(MessageNotification("cmd.craftan.locales", "%bc%We found the following locales:%bh% %locales%")),
    RELOAD_FILES_START(MessageNotification("cmd.craftan.reload", "%bc%Reloading locale files...")),
    RELOAD_FILES_FINISH(MessageNotification("cmd.craftan.finish", "%bc%Finished loading locale files...")),
}
