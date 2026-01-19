package de.craftan.engine.gameflow.turnstates

import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.action.CraftanGameAction
import de.craftan.engine.gameflow.action.RollDiceAction

class RollDiceState: TurnState() {
    override val name: String = "Roll dice"
    override val actions: List<CraftanGameAction<*,*>> = listOf(RollDiceAction())
}