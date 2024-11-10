package de.craftan.engine.gameflow.events.actions

import de.craftan.engine.CraftanEvent
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.map.DiceNumber

data class RolledDiceEvent(
    override val game: CraftanGame,
    val result: DiceNumber,
    val player: CraftanPlayer,
) : CraftanEvent(game)
