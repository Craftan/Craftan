package de.craftan.config.schema

import de.craftan.io.config.ConfigPath
import de.craftan.io.config.CraftanFileConfig
import de.craftan.io.config.annotations.Description
import de.craftan.io.config.annotations.Location
import de.craftan.io.config.annotations.Max
import de.craftan.io.config.annotations.Min

@ConfigPath("config/craftan.yml")
data class CraftanConfig(
    @Description("Enables single lobby mode - this means this server will stop itself as soon as the game ends.")
    val singleLobby: Boolean = false,

    val debug: Boolean = false,

    @Location("game.defaults")
    @Min(1.0) @Max(360.0)
    val turnDuration: Int = 60,

    @Location("game.defaults")
    @Min(1.0) @Max(360.0)
    val timeToDice: Int = 60,

    @Location("game.defaults")
    @Min(1.0)
    val pointsToWin: Int = 12,

    @Location("game.defaults")
    @Min(1.0)
    val cardsLimit: Int = 7,
) : CraftanFileConfig