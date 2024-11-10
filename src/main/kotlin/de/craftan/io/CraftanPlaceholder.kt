package de.craftan.io

/**
 * Models a placeholder in a craftan notification
 * @see resolveWithPlaceholder
 * @param placeholder the placeholder to string to use. Should look like ?your-placeholder
 */
enum class CraftanPlaceholder(
    val placeholder: String,
) {
    PLAYER("%player%"),
    BASE_COLOR("%bc%"),
    BASE_HIGHLIGHT("%bh%"),
    LOCALES("%locales%"),
}
