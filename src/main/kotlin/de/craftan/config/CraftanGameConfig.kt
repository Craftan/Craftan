package de.craftan.config

import de.craftan.io.config.*
import de.craftan.io.config.validators.validateResource
import de.craftan.io.config.CraftanConfig as ConfigBase

data class ColorConfig(
    val color: Int = default(0),
    val resource: String = PropertyBuilder<String>().validate("Must be minecraft resource!", ::validateResource).default(""),
    val displayName: String = default("")
)

data class CraftanGameConfig(
    val timeToRollOptions: List<Int> = default(listOf(30, 60, 90)),
    val turnTimeOptions: List<Int> = default(listOf(60, 90, 120)),
    val colors: Map<String, ColorConfig> = PropertyBuilder<Map<String, ColorConfig>>()
        .validate { it.size >= 4 }
        .comment("Defines the colors for the ingame teams. At least 4 colors are required.").default(mapOf(
            "Red" to ColorConfig(0xFF0000, "RED_WOOL", "Red"),
            "Blue" to ColorConfig(0x0000FF, "BLUE_WOOL", "Blue"),
            "Green" to ColorConfig(0x78ff00, "LIME_WOOL", "Green"),
            "Cyan" to ColorConfig(0x00ffcc, "LIGHT_BLUE_WOOL", "Cyan")
    ))
) : ConfigBase("config/game_settings.yml")
