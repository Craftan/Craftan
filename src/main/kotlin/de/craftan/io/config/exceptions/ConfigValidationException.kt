package de.craftan.io.config.exceptions

/**
 * Thrown when a configuration value from YAML violates declared validation constraints.
 * This is a fail-fast error to stop plugin boot with a clear message.
 */
class ConfigValidationException(
    val filePath: String,
    val yamlPath: String,
    val owner: String,
    val property: String,
    message: String,
) : RuntimeException("Invalid config value for $owner.$property at '$yamlPath' in '$filePath': $message")