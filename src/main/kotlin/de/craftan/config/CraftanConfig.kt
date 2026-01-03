package de.craftan.config

import de.craftan.io.config.*
import de.craftan.io.config.CraftanConfig as ConfigBase

data class GameDefaultsConfig(
    val turnDuration: Int = inRange(1..360).default(60),
    val timeToDice: Int = inRange(1..360).default(60),
    val pointsToWin: Int = min(3).default(12),
    val cardsLimit: Int = inRange(1..100).default(7)
)

data class GameConfig(
    val defaults: GameDefaultsConfig = default(GameDefaultsConfig())
)

data class CraftanConfig(
    val singleLobby: Boolean = default(false, comment = "Enables single lobby mode - this means this server will stop itself as soon as the game ends."),
    val debug: Boolean = default(false),
    val game: GameConfig = default(GameConfig())
) : ConfigBase("config/craftan.yml")
