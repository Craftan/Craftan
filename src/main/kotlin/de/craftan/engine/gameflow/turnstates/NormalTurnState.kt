package de.craftan.engine.gameflow.turnstates

import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.action.CraftanGameAction
import de.craftan.engine.gameflow.action.RollDiceAction

class NormalTurnState: TurnState() {
    override val name: String = "Normal turn"
    override val actions: List<CraftanGameAction<*,*>> = listOf(RollDiceAction())
}