package de.craftan.engine.states

import de.craftan.CraftanGameAction
import de.craftan.engine.CraftanGame
import de.craftan.engine.RoundState
import de.craftan.engine.actions.RollDiceAction
import de.craftan.engine.events.actions.RolledDiceEvent

class AwaitingDiceState(
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
