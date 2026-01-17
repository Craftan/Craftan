package de.craftan.config

import de.craftan.io.config.*
import de.craftan.io.config.CraftanConfig as ConfigBase

data class ScoreboardDefinition(
    val title: String,
    val lines: List<String>
)

data class ScoreboardConfig(
    val scoreboards: Map<String, ScoreboardDefinition> = flatten<Map<String, ScoreboardDefinition>>().default(
        mapOf(
            "waiting" to ScoreboardDefinition(
                "<gold><bold>Craftan",
                listOf(
                    "",
                    "<white>Players: <yellow>%current_players%/%max_players%",
                    "<white>Map: <yellow>%current_map%",
                    "<white>Status: <yellow>%lobby_status%",
                    "",
                    "<white>Color: %player_color%",
                    "",
                    "<gray>play.craftan.de"
                )
            ),
            "starting" to ScoreboardDefinition(
                "<gold><bold>Craftan",
                listOf(
                    "",
                    "<white>Players: <yellow>%current_players%/%max_players%",
                    "<white>Map: <yellow>%current_map%",
                    "<white>Status: <yellow>Starting...",
                    "",
                    "<white>Starting in: <yellow>%lobby_countdown%",
                    "<white>Color: %player_color%",
                    "",
                    "<gray>play.craftan.de"
                )
            ),
            "ingame" to ScoreboardDefinition(
                "<gold><bold>Craftan",
                listOf(
                    "",
                    "<white>Victory Points: <yellow>%victory_points%",
                    "<white>Resources: <yellow>%resources%",
                    "",
                    "<white>Round: <yellow>%round%",
                    "<white>Turn: <yellow>%turn_player%",
                    "",
                    "<gray>play.craftan.de"
                )
            )
        )
    )
) : ConfigBase("config/scoreboards.yml")
