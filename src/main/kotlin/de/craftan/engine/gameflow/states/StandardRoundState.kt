package de.craftan.engine.gameflow.states

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameAction
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.RoundState

class StandardRoundState(
    game: CraftanGame,
    player: CraftanPlayer
) : RoundState(game) {
    override val name: String = "Await placement of Structure..."

    override val actions: List<CraftanGameAction<*>> = listOf(TODO())

    override fun onTimeEnd() {
        TODO("Not yet implemented")
    }

    override fun setup() {
        TODO("Not yet implemented")
    }
}