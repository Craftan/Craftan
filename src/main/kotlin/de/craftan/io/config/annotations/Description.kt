package de.craftan.io.config.annotations

/**
 * Attaches a human-readable description as a comment to the YAML path of the annotated property.
 * This replaces the previous @Section concept. The comment is written next to the final property key.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(val value: String)
