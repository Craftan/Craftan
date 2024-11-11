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

    /**
     * Used for command information formatting
     */
    COMMAND_NAME("%command_name%"),
    COMMAND_ARGS("%command_args%"),
    COMMAND_DESCRIPTION("%command_description%"),
}
