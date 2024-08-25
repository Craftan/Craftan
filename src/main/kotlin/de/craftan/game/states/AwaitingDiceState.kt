package de.craftan.game.states

import de.craftan.game.CraftanGame
import de.craftan.game.CraftanGameAction
import de.craftan.game.CraftanRoundState
import de.craftan.game.actions.RollDiceAction

class AwaitingDiceState(
    override val game: CraftanGame,
) : CraftanRoundState {
    override val name: String = "Waiting for dice to be rolled"

    override val actions: List<CraftanGameAction<*>> = listOf(RollDiceAction(game))

    override fun nextState(): CraftanRoundState {
        TODO()
    }

    override fun canProceed(): Boolean = actions[0].result != null
}
