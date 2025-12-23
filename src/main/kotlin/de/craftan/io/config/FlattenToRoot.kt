package de.craftan.io.config

/**
 * Annotation to indicate that a Map<String, *> property should be flattened to the root of the YAML file.
 * This is particularly useful for key/value based configuration files like language files.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class FlattenToRoot
