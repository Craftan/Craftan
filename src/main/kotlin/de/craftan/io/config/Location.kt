package de.craftan.io.config

/**
 * Annotation to explicitly place a property at a custom YAML path.
 * Example: `@Location("config.game.here") val gameTime: Int = 60`
 * The annotated property's value will be read/written at that absolute path.
 * If present, this takes precedence over [FlattenToRoot].
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Location(val value: String)
