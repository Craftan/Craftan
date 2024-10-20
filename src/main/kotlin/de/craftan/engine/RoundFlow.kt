package de.craftan.engine

/**
 * Models a flow inside a round from state to state
 *
 * For example:
 *
 * Roll dice -> place settlement -> place road -> finished
 *
 * Because this flow is linear, a different round state interface can be passed to allow for a decision from the state.
 * @see DecidableState
 *
 * For a good example take a look at the [de.craftan.engine.flows] directory
 *
 * @param startState the starting state, which can also be the end state
 * @param game the game this flow is currently using
 */
abstract class RoundFlow(
    val startState: RoundState,
    val game: CraftanGame,
) {
    val states = mutableListOf(startState)

    private var stateIndex = 0

    /**
     * Shows the current state of this flow
     */
    var currentState = startState
        private set

    init {
        currentState.flow = this
        startState.setup()
    }

    fun addState(state: RoundState) {
        states += state
    }

    /**
     * Goes to the next state of this flow, or finishes the flow if the last state was reached
     *
     * Depending on if the current state is a [DecidableState] the outcome will change.
     * If the current state is a decidable-state, the [DecidableState.nextState] function will be used to insert
     * a state into the chain, before handling the state afterward.
     */
    fun nextState() {
        stateIndex++
        if (stateIndex > states.size - 1) {
            return finishFlow()
        }

        currentState.let { if (it is DecidableState) states.add(stateIndex, it.nextState()) }

        currentState.cleanUp()
        currentState = states[stateIndex]

        currentState.flow = this
        currentState.setup()
    }

    /**
     * Finished the flow by notifying the game
     */
    open fun finishFlow() {
        game.nextRound()
    }
}
