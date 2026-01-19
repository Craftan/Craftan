package de.craftan.io

/**
 * Models a placeholder in a craftan notification
 * @see resolveWithPlaceholder
 * @param placeholder the placeholder to string to use. Should look like ?your-placeholder
 */
enum class CraftanPlaceholder(
    val placeholder: String,
) {
    PREFIX("%prefix%"),
    PLAYER("%player%"),
    BASE_COLOR("%bc%"),
    BASE_HIGHLIGHT("%bh%"),
    LOCALES("%locales%"),
    COUNT("%count%"),

    /**
     * Config placeholder
     */
    KEY("%key%"),
    CONFIG("%config%"),
    VALUE("%value%"),

    /**
     * Used for command information formatting
     */
    COMMAND_NAME("%command_name%"),
    COMMAND_ARGS("%command_args%"),
    COMMAND_DESCRIPTION("%command_description%"),
    COMMAND_SUBCOMMAND_INFO("%command_subcommands_info%"),
    COMMAND_SUBCOMMANDS("%command_subcommands%"),

    /**
     * Used for /craftan lobby list
     */
    LOBBY_ID("%lobby_id%"),
    CURRENT_PLAYERS("%current_players%"),
    MAX_PLAYERS("%max_players%"),
    CURRENT_MAP("%current_map%"),

    /**
     * Used for Config Inventory
     */
    DICE_TIME("%time_to_roll_dice%"),
    OPTION_SELECTED("%option_selected%"),
    POINTS_TO_WIN("%points_to_win%"),
    TURN_TIME("%turn_time%"),
    CARDS_LIMIT("%card_limit%"),

    /**
     * Used for lobby notifications
     */
    TIME_TO_START("%time_to_start%"),

    /**
     * Scoreboard placeholders
     */
    LOBBY_STATUS("%lobby_status%"),
    STATUS("%status%"),
    LOBBY_COUNTDOWN("%lobby_countdown%"),
    PLAYER_COLOR("%player_color%"),
    VICTORY_POINTS("%victory_points%"),
    RESOURCES("%resources%"),
    RESOURCES_LUMBER("%resources_lumber%"),
    RESOURCES_WOOL("%resources_wool%"),
    RESOURCES_GRAIN("%resources_grain%"),
    RESOURCES_BRICK("%resources_brick%"),
    RESOURCES_ORE("%resources_ore%"),
    ROUND("%round%"),
    TURN_PLAYER("%turn_player%"),
    CURRENT_TURN("%current_turn%"),
}
