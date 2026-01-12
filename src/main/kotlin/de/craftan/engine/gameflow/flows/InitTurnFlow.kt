package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.turnstates.PlaceRoadState
import de.craftan.engine.gameflow.turnstates.PlaceSettlementState
import net.ormr.eventbus.EventBus

class InitTurnFlow(
    eventBus: EventBus<Any, CraftanGameEvent>,
    round: GameRound
) : TurnFlow(PlaceSettlementState(), eventBus, round) {
    init {
        putStateTransition(
            startState, mutableListOf(PlaceRoadState()),
            chooser = { _: CraftanGameEvent, _: TurnState, states: List<TurnState> -> states.first() },
        )
    }
}