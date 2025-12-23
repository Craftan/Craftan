package de.craftan.io.config

internal fun joinPath(prefix: String, child: String): String = when {
    prefix.isEmpty() -> child
    child.isEmpty() -> prefix
    else -> "$prefix.$child"
}

/**
 * Resolves the effective YAML path for a property, honoring precedence:
 * 1) @Location absolute path if present
 * 2) @FlattenToRoot keeps current prefix
 * 3) @Section joins its path with current prefix and then the property name
 * 4) default: prefix + property name
 */
internal fun resolvePropertyPath(
    prefix: String,
    propName: String,
    location: String?,
    section: String?,
    flatten: Boolean,
): String {
    val normalizedLocation = normalizeLocation(location)
    if (!normalizedLocation.isNullOrEmpty()) return normalizedLocation
    if (flatten) return prefix
    val normalizedSection = normalizeLocation(section)
    val base = if (!normalizedSection.isNullOrEmpty()) joinPath(prefix, normalizedSection) else prefix
    return joinPath(base, propName)
}

internal fun normalizeLocation(loc: String?): String? {
    if (loc == null) return null
    val trimmed = loc.trim().trim('.')
    return trimmed
}
