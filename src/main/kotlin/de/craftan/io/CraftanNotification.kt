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
            %bc%Usage: %bh%/%command_name% %command_args%
            %bc%Description: %bh%%command_description%
            %bc%Subcommands: %command_subcommands_info%
            %command_subcommands%
            %bc%<st>⎯⎯⎯</st>%prefix%%bc%<st>⎯⎯⎯</st>
            """.trimIndent(),
            false,
        ),
    ),
    COMMAND_SUBCOMMAND_PREFIX(MessageNotification("cmd.craftan.info.subcommand_prefix", "-", false)),
    COMMAND_SUBCOMMANDS_NONE(MessageNotification("cmd.craftan.info.subcommands_none", "This command has no subcommands", false)),
    COMMAND_SUBCOMMANDS_COUNT(MessageNotification("cmd.craftan.info.subcommands_count", "This command has %count% subcommands", false)),

    LOCALES(MessageNotification("cmd.craftan.locales", "%bc%We found the following locales:%bh% %locales%")),
    LOCALES_CREATE_FAILED(MessageNotification("cmd.craftan.locales.create.failed", "%bc%There is already a file for the given localization.")),
    LOCALES_CREATE_SUCCESS(
        MessageNotification("cmd.craftan.locales.create.success", "%bc%The file for the desired locale has been created. Be sure to %bh%<hover:show_text:'Click to insert command'><click:suggest_command:'/craftan messages reload'>reload</click></hover> the localizations."),
    ),

    RELOAD_FILES_START(MessageNotification("cmd.craftan.reload", "%bc%Reloading locale files...")),
    RELOAD_FILES_FINISH(MessageNotification("cmd.craftan.finish", "%bc%Finished loading locale files...")),
}
