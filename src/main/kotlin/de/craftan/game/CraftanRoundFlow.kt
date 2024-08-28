package de.craftan.game

/**
 * Models a flow inside a round from state to state
 *
 * For example:
 *
 * Roll dice -> place settlement -> place road -> finished
 *
 * @param startState the starting state, which can also be the end state
 * @param game the game this flow is currently using
 */
abstract class CraftanRoundFlow(
    startState: CraftanRoundState,
    val game: CraftanGame,
) {
    private val states = mutableListOf(startState)

    /**
     * Shows the current state of this flow
     */
    var currentState = startState
        private set

    init {
        startState.setup()
    }

    private var stateIndex = 0

    /**
     * Goes to the next state of this flow, or finishes the flow if the last state was reached
     */
    fun nextState() {
        stateIndex++

        if (stateIndex > states.size - 1) {
            return finishFlow()
        }

        currentState.cleanUp()
        currentState = states[stateIndex]
        currentState.setup()
    }

    /**
     * Finished the flow by notifying the game
     */
    fun finishFlow() {
        game.nextRound()
    }

    /**
     * Adds a new state to the game
     */
    fun addState(state: CraftanRoundState) {
        states += state
    }

    /**
     * @return the last state of the flow
     */
    fun endState() = states.last()
}
