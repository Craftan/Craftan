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
    CURRENT_MAP("%current_map%")

}
