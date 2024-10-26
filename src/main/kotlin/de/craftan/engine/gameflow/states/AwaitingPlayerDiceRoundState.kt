package de.craftan.engine.gameflow.states

import de.craftan.CraftanGameAction
import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.RoundState
import de.craftan.engine.gameflow.actions.RollDiceAction
import de.craftan.engine.gameflow.events.actions.RolledDiceEvent

class AwaitingPlayerDiceRoundState(
    game: CraftanGame,
) : RoundState(game) {
    override val name: String = "Waiting for dice to be rolled"

    private val rollDiceAction = RollDiceAction(game)

    override val actions: List<CraftanGameAction<*>> = listOf(rollDiceAction)

    override fun onTimeEnd() {
        TODO("Not yet implemented")
    }

    override fun setup() {
        rollDiceAction.eventBus.on<RolledDiceEvent> {
            eventBus.fire(this)
        }
    }
}
