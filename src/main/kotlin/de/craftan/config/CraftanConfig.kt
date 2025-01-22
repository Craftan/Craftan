package de.craftan.config

import com.uchuhimo.konf.ConfigSpec

object CraftanConfig : ConfigSpec("craftan") {
    val singleLobby by optional<Boolean>(
        true,
        description = "Whether craftan should handle this server as a single lobby, or allow multiple lobbies per this server.",
    )

    val defaultPointsToWin by optional<Int>(
        default = 10,
        description = "How many points are required to win a game, by default. This can be changed per game"
    )

    val defaultDiscardLimit by optional<Int>(
        default = 7,
        description = "Default limit of how many cards a player can hold max before discarding. This can be changed per game"
    )

}
