package de.craftan.config.schema

import de.craftan.io.config.ConfigPath
import de.craftan.io.config.CraftanFileConfig
import de.craftan.io.config.annotations.Description
import de.craftan.io.config.annotations.Length
import de.craftan.io.config.annotations.Location
import de.craftan.io.config.annotations.Resource

@ConfigPath("config/game_settings.yml")
data class CraftanGameConfig(
    @Length(3, 3)
    @Location("configuration.inventory")
    val timeToRollOptions: List<Int> = listOf(30, 60, 90),

    @Length(3, 3)
    @Location("configuration.inventory")
    val turnTimeOptions: List<Int> = listOf(60, 90, 120),

    @Description("Defines the colors for the ingame teams. Atleast 4 colors are required.")
    @Length(4)
    val colors: Map<String, ColorConfig> = mapOf(
        "Red" to ColorConfig(0xFF0000, "RED_WOOL"),
        "Blue" to ColorConfig(0x0000FF, "BLUE_WOOL"),
        "Green" to ColorConfig(0x78ff00, "LIME_WOOL"),
        "Cyan" to ColorConfig(0x00ffcc, "LIGHT_BLUE_WOOL")),

): CraftanFileConfig

data class ColorConfig(val color: Int, @Resource val resource: String)