package de.craftan.game.events

import de.craftan.game.CancelableGameEvent
import de.craftan.game.CraftanGame

data class GameStartEvent(
    override val game: CraftanGame,
) : CancelableGameEvent(game)
