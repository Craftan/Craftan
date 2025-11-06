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

    JOINED_GAME(MessageNotification("lobby.joined", "%bc%The player %player% just joined the game!")),
    LEFT_GAME(MessageNotification("lobby.left", "%bc%The player %player% just left the game!")),

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
    COMMAND_SUBCOMMAND_PREFIX(MessageNotification("cmd.craftan.info.subcommand_prefix", ">", false)),
    COMMAND_SUBCOMMANDS_NONE(MessageNotification("cmd.craftan.info.subcommands_none", "This command has no subcommands", false)),
    COMMAND_SUBCOMMANDS_COUNT(MessageNotification("cmd.craftan.info.subcommands_count", "This command has %count% subcommands", false)),

    LIST_LOBBIES_EMPTY(MessageNotification("cmd.craftan.lobbies.list.empty", "%bc%There are no lobbies yet.")),
    LIST_LOBBIES_INTRO(MessageNotification("cmd.craftan.lobbies.list.intro", "%bc%<st>⎯⎯⎯</st>%prefix%%bc%<st>⎯⎯⎯</st>")),
    LIST_LOBBIES_ENTRY(MessageNotification("cmd.craftan.lobbies.list.entry", "%bc%--> %bh%%lobby_id% %bc%- %current_players%%bh%/%bc%%max_players% %bc%- %current_map%")),

    LOCALES(MessageNotification("cmd.craftan.locales.list", "%bc%We found the following locales:%bh% %locales%")),
    LOCALES_CREATE_FAILED(MessageNotification("cmd.craftan.locales.create.failed", "%bc%There is already a file for the given localization.")),
    LOCALES_CREATE_SUCCESS(
        MessageNotification("cmd.craftan.locales.create.success", "%bc%The file for the desired locale has been created. Be sure to %bh%<hover:show_text:'Click to insert command'><click:suggest_command:'/craftan messages reload'>reload</click></hover> the localizations."),
    ),

    RELOAD_FILES_START(MessageNotification("cmd.craftan.reload", "%bc%Reloading locale files...")),
    RELOAD_FILES_FINISH(MessageNotification("cmd.craftan.finish", "%bc%Finished loading locale files...")),

    //Lobby Config
    LOBBY_CONFIG_INVENTORY_TITLE(MessageNotification("lobby.config.inventory.title", "%bh% >> %bc%Lobby Config for new game", false)),

    LOBBY_CONFIG_INVENTORY_OPTION_SELECTED(MessageNotification("lobby.config.inventory.option_selected", "<green>[Selected]", false)),

    LOBBY_CONFIG_INVENTORY_EXTENSION_OPTION(MessageNotification("lobby.config.inventory.extensions_option", "%bc%Extensions", false)),
    LOBBY_CONFIG_INVENTORY_EXTENSION(MessageNotification("lobby.config.inventory.extension", "%option_selected% %bc%%extension_name%", false)),

    LOBBY_CONFIG_INVENTORY_TIME_DICE_OPTION(MessageNotification("lobby.config.inventory.time_to_roll_dice_option", "%bc%Time to roll the dice", false)),
    LOBBY_CONFIG_INVENTORY_TIME_DICE(MessageNotification("lobby.config.inventory.time_to_roll_dice", "%option_selected% %bh%>> %bc%%time_to_roll_dice%", false)),

    LOBBY_CONFIG_INVENTORY_TIME_TURN_OPTION(MessageNotification("lobby.config.inventory.turn_time_option", "%bc%Turn Time", false)),
    LOBBY_CONFIG_INVENTORY_TIME_TURN(MessageNotification("lobby.config.inventory.turn_time", "%option_selected% %bh%>> %bc%%turn_time%", false)),

    LOBBY_CONFIG_INVENTORY_POINTS_TO_WIN_OPTION(MessageNotification("lobby.config.inventory.points_to_win.option", "%bc%Points to win", false)),
    LOBBY_CONFIG_INVENTORY_POINTS_TO_WIN(MessageNotification("lobby.config.inventory.points_to_win.display", "%bh%>> %bc%%points_to_win%", false)),
    LOBBY_CONFIG_INVENTORY_POINTS_TO_WIN_PLUS(MessageNotification("lobby.config.inventory.points_to_win.add", "<green>+1", false)),
    LOBBY_CONFIG_INVENTORY_POINTS_TO_WIN_MINUS(MessageNotification("lobby.config.inventory.points_to_win.minus", "<red>-1", false)),

    LOBBY_CONFIG_INVENTORY_CARDS_LIMIT_OPTION(MessageNotification("lobby.config.inventory.card_limit.option", "%bc%Discard Limit", false)),
    LOBBY_CONFIG_INVENTORY_CARDS_LIMIT(MessageNotification("lobby.config.inventory.card_limit.display", "%bh%>> %bc%%card_limit%", false)),
    LOBBY_CONFIG_INVENTORY_CARDS_LIMIT_PLUS(MessageNotification("lobby.config.inventory.card_limit.plus", "<green>+1", false)),
    LOBBY_CONFIG_INVENTORY_CARDS_LIMIT_MINUS(MessageNotification("lobby.config.inventory.card_limit.minus", "<red>-1", false)),

    LOBBY_CONFIG_INVENTORY_CREATE_LOBBY(MessageNotification("lobby.config.inventory.create_lobby", "<green>Create new lobby", false)),

    LOBBY_CONFIG_INVENTORY_CLOSED(MessageNotification("lobby.config.inventory.back", "%bc%We saved your configuration for the next time. %bh%<click:run_command:'/craftan lobby create'><b>REOPEN</b></click> <red><click:run_command:'/craftan lobby clear'><b>CLEAR</b></click>")),
    LOBBY_CONFIG_CLEARED(MessageNotification("lobby.config.cleared", "%bc%We cleared your configuration.")),

    LOBBY_CREATED(MessageNotification("lobby.created", "%bc%The lobby has been created."))
}
