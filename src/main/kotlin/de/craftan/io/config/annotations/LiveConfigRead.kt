package de.craftan.io.config.annotations

/**
 * Opt-in marker for APIs that read configuration from disk on every call.
 *
 * Rationale: ConfigFile.get() and Configs.get() are designed to always load/merge the YAML file
 * from disk when invoked, so callers are fully up-to-date and updates are applied automatically.
 * This implies I/O on each invocation. Callers should consciously opt in to this behavior.
 */
@RequiresOptIn(
    message = "This call reads the configuration from disk on each invocation. Consider caching if you need performance.")
@Retention(AnnotationRetention.BINARY)
annotation class LiveConfigRead