package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.events.actions.PlacedStructureEvent
import de.craftan.engine.gameflow.states.AwaitStructurePlacement
import de.craftan.engine.structures.Road
import de.craftan.engine.structures.Settlement

/**
 * Models how a round flow should look like in the setup stage of the game,
 * where the players place their first settlements and roads
 */
class InitRoundFlow(
    game: CraftanGame,
    round:GameRound
) : RoundFlow(AwaitStructurePlacement(game, Settlement()), game, round) {
    init {
        addState(AwaitStructurePlacement(game, Road()))
        states.forEach { it.eventBus.on<PlacedStructureEvent> { nextState() } }
    }
}
