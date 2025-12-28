package de.craftan.engine.gameflow.flows

import de.craftan.engine.gameflow.turnstates.PlaceRoadState
import de.craftan.engine.gameflow.turnstates.PlaceSettlementState

class InitTurnFlow : TurnFlow(PlaceSettlementState()) {
    init {
        putStateTransition(
            PlaceSettlementState(), mutableListOf(PlaceRoadState()),
            chooser = { PlaceRoadState() },
        )
    }
}