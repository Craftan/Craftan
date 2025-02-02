package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.events.actions.PlacedStructureEvent
import de.craftan.engine.gameflow.states.AwaitStructurePlacementRoundState
import de.craftan.engine.structures.Road
import de.craftan.engine.structures.Settlement

/**
 * Models how a round flow should look like in the setup stage of the game,
 * where the players place their first settlements and roads
 */
class InitRoundFlow(
    game: CraftanGame,
    round:GameRound
) : RoundFlow(game, round) {
    init {
        addState(AwaitStructurePlacementRoundState(game, Settlement()))
        addState(AwaitStructurePlacementRoundState(game, Road()))
        states.forEach { it.eventBus.on<PlacedStructureEvent> {
            player.inventory.remove(structureInfo.cost)
            player.inventory.remove(structureInfo)
            game.map.placeStructure(structureInfo, coordinates, direction, player)
            nextState()
        } }
    }
}
