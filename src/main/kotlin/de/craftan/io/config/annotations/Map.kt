package de.craftan.io.config.annotations

/**
 * Alias for [MapKey]; specifies the target key when emitting a property into a YAML map.
 * See @MapKey for details and examples.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Map(val value: String)
