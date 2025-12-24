package de.craftan.io.config

/**
 * Declares the default relative file path (from the plugin data folder) for a config class.
 * Example: @ConfigPath("config/craftan.yml")
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigPath(val value: String)
