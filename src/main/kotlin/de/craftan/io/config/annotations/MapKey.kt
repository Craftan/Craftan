package de.craftan.io.config.annotations

/**
 * Indicates that the annotated property should be written under a specific key of a YAML map.
 *
 * Usage example to produce:
 * colors:
 *   red:
 *     - 0xFF0000
 *     - RED_WOOL
 *
 * data class ExampleConfig(
 *   @Location("colors")
 *   @MapKey("red")
 *   val red: List<String> = listOf("0xFF0000", "RED_WOOL"),
 * )
 *
 * Works together with @Location which defines the parent path. The property name is NOT used in the
 * final YAML path when @MapKey is present.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class MapKey(val value: String)
