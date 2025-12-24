package de.craftan.io.config

internal fun joinPath(prefix: String, child: String): String = when {
    prefix.isEmpty() -> child
    child.isEmpty() -> prefix
    else -> "$prefix.$child"
}

/**
 * Resolves the effective YAML path for a property, honoring precedence and @MapKey:
 * 1) @Location defines a parent path
 * 2) @FlattenToRoot keeps current prefix (property appears directly under the prefix)
 * 3) default: prefix + property name
 *
 * If [mapKey] is provided, the final path becomes `<parent>.<mapKey>` and the property name is not used.
 */
internal fun resolvePropertyPath(
    prefix: String,
    propName: String,
    location: String?,
    flatten: Boolean,
    mapKey: String?
): String {
    val loc = normalizeLocation(location)
    val key = normalizeLocation(mapKey)

    // Base parent path resolution
    val base = when {
        !loc.isNullOrEmpty() -> loc
        flatten -> prefix
        else -> joinPath(prefix, propName)
    }

    // If @Location is present and no @MapKey, we must append the property name
    if (!loc.isNullOrEmpty() && key.isNullOrEmpty()) {
        return joinPath(loc, propName)
    }

    // If @MapKey is provided, append it to the base
    return if (!key.isNullOrEmpty()) joinPath(base, key) else base
}

internal fun normalizeLocation(loc: String?): String? {
    if (loc == null) return null
    val trimmed = loc.trim().trim('.')
    return trimmed
}
