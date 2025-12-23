package de.craftan.io.config

import org.bukkit.configuration.file.FileConfiguration

/**
 * Ensures that a YAML section exists at the given path and attaches a single-line comment to it
 * if the underlying FileConfiguration implementation supports comments.
 * This uses reflection to avoid hard dependency on newer Bukkit APIs.
 */
internal fun ensureSectionAndComment(out: FileConfiguration, sectionPath: String, comment: String) {
    val normalized = normalizeLocation(sectionPath) ?: return
    if (!out.contains(normalized)) {
        try {
            out.createSection(normalized)
        } catch (_: Exception) {
            // ignore
        }
    }
    try {
        val m = out.javaClass.methods.firstOrNull {
            it.name == "setComments" && it.parameterTypes.size == 2 && it.parameterTypes[0] == String::class.java
        }
        m?.invoke(out, normalized, listOf(comment))
    } catch (_: Exception) {
        // ignore if comments not supported
    }
}
