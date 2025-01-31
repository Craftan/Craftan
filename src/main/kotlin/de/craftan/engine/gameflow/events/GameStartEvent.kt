package de.craftan.engine.gameflow.events

import de.craftan.engine.CancelableCraftanEvent
import de.craftan.engine.CraftanGame

data class GameStartEvent(
    override val game: CraftanGame,
) : CancelableCraftanEvent(game)
