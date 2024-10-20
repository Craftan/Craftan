package de.craftan.engine.gameflow.states

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.RoundState

/**
 * Can be used to manipulate the flow of a round
 * @see RoundFlow.nextState
 */
abstract class DecidableState(
    game: CraftanGame,
) : RoundState(game) {
    abstract fun nextState(): RoundState
}
