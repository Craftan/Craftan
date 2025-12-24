package de.craftan.io.config.annotations

/**
 * Deprecated: Replaced by @Description which writes a comment next to the final property key.
 * This annotation is retained only for source compatibility and is no longer used by the adapter.
 */
@Deprecated("Replaced by @Description. Use @Description for per-key comments.", level = DeprecationLevel.WARNING)
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Section(val value: String, val comment: String = "")
