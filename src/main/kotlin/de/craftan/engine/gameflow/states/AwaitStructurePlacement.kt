package de.craftan.engine.gameflow.states

import de.craftan.CraftanGameAction
import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.RoundState
import de.craftan.engine.gameflow.actions.PlaceStructureAction
import de.craftan.engine.map.graph.StructureInfo

/**
 * The state in a round when the game awaits the placement of a structure from a player
 */
class AwaitStructurePlacement(
    game: CraftanGame,
    allowedStructures: Set<StructureInfo>? = null, // null is to be interpreted as all structures are allowed
) : RoundState(game) {
    override val name: String = "Await placement of Structure..."

    override val actions: List<CraftanGameAction<*>> = listOf(PlaceStructureAction(game))

    override fun onTimeEnd() {
        TODO("Not yet implemented")
    }

    override fun setup() {
        TODO("Not yet implemented")
    }
}
