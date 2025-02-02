package de.craftan.engine.gameflow.states

import de.craftan.engine.CraftanGameAction
import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.RoundState
import de.craftan.engine.gameflow.actions.PlaceStructureAction
import de.craftan.engine.structures.CraftanStructure

/**
 * The state in the pregame round when the game awaits the placement of a structure from a player.
 */
class AwaitStructurePlacementRoundState(
    game: CraftanGame,
    allowedStructure: CraftanStructure,
) : RoundState(game) {
    override val name: String = "Await placement of Structure..."

    override val actions: List<CraftanGameAction<*>> = listOf(PlaceStructureAction(game,allowedStructure, game.playerSequence.currentPlayer))

    override fun onTimeEnd() {
        TODO("Not yet implemented")
    }

    override fun setup() {
        TODO("Not yet implemented")
    }
}
