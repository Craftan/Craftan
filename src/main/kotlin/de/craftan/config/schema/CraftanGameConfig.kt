package de.craftan.config.schema

import de.craftan.io.config.PropertyBuilder
import de.craftan.io.config.default
import de.craftan.io.config.validators.validateResource
import net.kyori.adventure.text.format.NamedTextColor
import de.craftan.io.config.CraftanConfig as ConfigBase

data class ColorConfig(
    val color: String = PropertyBuilder<String>().validate { NamedTextColor.NAMES.value(it) != null }.comment("This MUST be a NamedTextColor").default(""),
    val resource: String = PropertyBuilder<String>().validate("Must be minecraft resource!", ::validateResource).default(""),
    val displayName: String = default("")
)

data class CraftanGameConfig(
    val timeToRollOptions: List<Int> = default(listOf(30, 60, 90)),
    val turnTimeOptions: List<Int> = default(listOf(60, 90, 120)),
    val colors: Map<String, ColorConfig> = PropertyBuilder<Map<String, ColorConfig>>()
        .validate { it.size >= 4 }
        .comment("Defines the colors for the ingame teams. At least 4 colors are required.").default(mapOf(
            "Red" to ColorConfig("red", "RED_WOOL", "Red"),
            "Blue" to ColorConfig("dark_blue", "BLUE_WOOL", "Blue"),
            "Green" to ColorConfig("green", "LIME_WOOL", "Green"),
            "Cyan" to ColorConfig("blue", "LIGHT_BLUE_WOOL", "Cyan")
    ))
) : ConfigBase("config/game_settings.yml")
