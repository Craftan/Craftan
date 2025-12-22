package de.craftan.engine.gameflow.events

import de.craftan.engine.CancelableCraftanGameEvent
import de.craftan.engine.CraftanGame

data class GameStartGameEvent(
    override val game: CraftanGame,
) : CancelableCraftanGameEvent(game)
