package de.craftan.engine.gameflow.events.actions

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.map.DiceNumber

data class RolledDiceGameEvent(
    override val game: CraftanGame,
    val result: DiceNumber,
    val player: CraftanPlayer,
) : CraftanGameEvent(game)
