package de.craftan.engine.gameflow.turnstates

import de.craftan.engine.gameflow.action.CraftanGameAction
import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.action.PlaceStructureAction

class PlaceSettlementState: TurnState() {
    override val name: String = "Place settlement"
    override val actions: List<CraftanGameAction<*>> = listOf(PlaceStructureAction(false)) //TODO
}