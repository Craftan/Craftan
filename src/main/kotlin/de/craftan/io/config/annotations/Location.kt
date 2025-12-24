package de.craftan.io.config.annotations

/**
 * Annotation to explicitly place a property under a custom YAML parent path.
 * Example: `@Location("config.game.defaults") val gameTime: Int = 60` → writes to `config.game.defaults.gameTime`
 * The annotated property's value will be read/written under this parent path with the property name appended.
 * If present, this takes precedence over [FlattenToRoot].
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Location(val value: String)
