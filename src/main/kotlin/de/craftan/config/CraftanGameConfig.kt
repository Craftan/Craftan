package de.craftan.config

import de.craftan.io.config.*
import de.craftan.io.config.validators.validateResource
import de.craftan.io.config.CraftanConfig as ConfigBase

data class ColorConfig(
    val color: Int = default(0),
    val resource: String = PropertyBuilder<String>().validate("Must be minecraft resource!", ::validateResource).default("")
)

data class CraftanGameConfig(
    val timeToRollOptions: List<Int> = default(listOf(30, 60, 90)),
    val turnTimeOptions: List<Int> = default(listOf(60, 90, 120)),
    val colors: Map<String, ColorConfig> = default(mapOf(
        "Red" to ColorConfig(0xFF0000, "RED_WOOL"),
        "Blue" to ColorConfig(0x0000FF, "BLUE_WOOL"),
        "Green" to ColorConfig(0x78ff00, "LIME_WOOL"),
        "Cyan" to ColorConfig(0x00ffcc, "LIGHT_BLUE_WOOL")
    ), comment = "Defines the colors for the ingame teams. At least 4 colors are required.")
) : ConfigBase("config/game_settings.yml")
