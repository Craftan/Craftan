package de.craftan.engine.gameflow.turnstates

import de.craftan.engine.gameflow.action.CraftanGameAction
import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.action.CraftanActionData
import de.craftan.engine.gameflow.action.PlaceStructureAction

class PlaceRoadState: TurnState() {
    override val name: String = "Place road"
    override val actions: List<CraftanGameAction<out CraftanActionData, *>> = listOf(PlaceStructureAction(false))
}