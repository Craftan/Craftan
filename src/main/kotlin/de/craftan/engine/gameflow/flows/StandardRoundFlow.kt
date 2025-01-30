package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.events.actions.PlacedStructureEvent
import de.craftan.engine.gameflow.events.actions.RolledDiceEvent
import de.craftan.engine.gameflow.states.AwaitStructurePlacement
import de.craftan.engine.gameflow.states.AwaitingDiceRoundState
import de.craftan.engine.structures.Road
import de.craftan.engine.structures.Settlement

class StandardRoundFlow(
    game: CraftanGame,
    round: GameRound
) : RoundFlow(AwaitingDiceRoundState(game), game, round) {
    init {
        states.last().eventBus.on<RolledDiceEvent> {
            TODO("Handle Dice Roll")
        }
        states.forEach {
            it.eventBus.on<PlacedStructureEvent> {
                nextState()
            }
        }
    }
}