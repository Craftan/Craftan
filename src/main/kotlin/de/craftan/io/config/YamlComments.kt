package de.craftan.io.config

import org.bukkit.configuration.file.FileConfiguration

/**
 * Attaches a single-line comment to the given YAML path if supported by the underlying implementation.
 * Uses reflection to call setComments(String, List<String>) when available.
 */
internal fun setComment(out: FileConfiguration, path: String, comment: String) {
    val normalized = normalizeLocation(path) ?: return
    try {
        val m = out.javaClass.methods.firstOrNull {
            it.name == "setComments" && it.parameterTypes.size == 2 && it.parameterTypes[0] == String::class.java
        }
        m?.invoke(out, normalized, listOf(comment))
    } catch (_: Exception) {
        // ignore if comments not supported
    }
}
