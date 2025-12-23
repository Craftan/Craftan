package de.craftan.io.config

/**
 * Annotation to group a property under a named YAML section and optionally attach a comment to that section.
 * Example:
 *   @Section("game.defaults", "Default game-related values")
 *   val turnDuration: Int = 60
 *
 * The section path is joined with the current prefix, unless @Location is present (which takes precedence).
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Section(val value: String, val comment: String = "")
