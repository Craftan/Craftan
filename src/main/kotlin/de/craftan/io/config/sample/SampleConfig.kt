package de.craftan.io.config.sample

import de.craftan.io.config.FlattenToRoot
import de.craftan.io.config.Location

/**
 * Example configuration schema demonstrating supported shapes.
 */
data class SampleConfig(
    val version: Int = 1,
    val enabled: Boolean = true,
    val title: String = "Hello Craftan",
    val modes: List<String> = listOf("a", "b"),
    @FlattenToRoot
    val colors: Map<String, String> = mapOf("base" to "#ffffff", "accent" to "#00c9a5"),
    val logLevel: LogLevel = LogLevel.INFO,
    val nested: Nested = Nested(),

    @Location("config.game.timers")
    val gameTime: Int = 60
) {
    data class Nested(
        val retries: Int = 3,
        val timeoutSeconds: Long = 15,
    )

    enum class LogLevel { TRACE, DEBUG, INFO, WARN, ERROR }
}
