package de.craftan.io.config.annotations

/**
 * Simple validation annotations for config properties.
 * - Min/Max apply to numeric types (Int, Long, Double)
 * - Length applies to String and List sizes
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Min(val value: Double)

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Max(val value: Double)

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Length(val min: Int = 0, val max: Int = Int.MAX_VALUE)
