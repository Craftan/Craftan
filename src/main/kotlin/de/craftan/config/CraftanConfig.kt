package de.craftan.config

import de.craftan.io.config.CraftanFileConfig
import de.craftan.io.config.Location
import de.craftan.io.config.ConfigPath

@ConfigPath("config/craftan.yml")
data class CraftanConfig(
    val singleLobby: Boolean = false,

    @Location("game.defaults")
    val turnDuration: Int = 60,

    @Location("game.defaults")
    val timeToDice: Int = 60,

    @Location("game.defaults")
    val pointsToWin: Int = 7,

) : CraftanFileConfig