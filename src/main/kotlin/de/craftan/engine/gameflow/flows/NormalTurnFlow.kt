package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.gameflow.rounds.GameRound
import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.turnstates.NormalTurnState
import de.craftan.engine.gameflow.turnstates.PlaceRoadState
import de.craftan.engine.gameflow.turnstates.PlaceSettlementState
import de.craftan.engine.gameflow.turnstates.RollDiceState

class NormalTurnFlow (
    round: GameRound
) : TurnFlow(RollDiceState(), round) {
    init {
        putStateTransition(
            startState, mutableListOf(NormalTurnState()),
            chooser = { _, _, states: List<TurnState>, _ -> states.first() },
        )
    }
}