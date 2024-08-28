package de.craftan.engine

/**
 * Can be used to manipulate the flow of a round
 * @see RoundFlow.nextState
 */
abstract class DecidableState(
    game: CraftanGame,
) : RoundState(game) {
    abstract fun nextState(): RoundState
}
