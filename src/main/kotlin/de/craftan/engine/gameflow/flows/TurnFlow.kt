package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.gameflow.rounds.GameRound
import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.action.CraftanGameActionEvent

/**
 * Models a flow inside a turn from state to state
 *
 * For example:
 *
 * Roll dice -> place settlement -> place road -> finished
 *
 *
 * @param startState the starting state, which can also be the end state
 */
abstract class TurnFlow(
    val startState: TurnState,
    val round: GameRound
) {
    /**
     * Shows the current state of this flow
     */
    var state = startState
        private set

    val graph = Graph()

    fun putStateTransition(from: TurnState, to: MutableList<TurnState>, chooser: (CraftanGameEvent, TurnState, List<TurnState>, Any?) -> TurnState?) {
        graph.put(from, to, chooser)
    }

    /**
     * Goes to the next state of this flow, or finishes the flow if the last state was reached
     */
    // Listen to a Endstate eventbus
    fun nextState(event: CraftanGameActionEvent<*, *, *>, result: Any?) {
        // TODO This means the state shouldn't change just yet
        val neighbors = graph.neighbors(state)
        println("${state.name} -> ${neighbors.joinToString()}")
        if (neighbors.isEmpty()) {
            finishFlow()
            return
        }
        val nextState = graph.getChooser(state).invoke(event, state, neighbors, result) ?: return
        state = nextState
    }

    /**
     * Finished the flow by notifying the game
     */
    fun finishFlow() {
        println("Finish Turnflow")
        round.nextTurn()
    }
}

//TODO Rename
data class Graph(
    val adjacency: MutableMap<TurnState, Pair<List<TurnState>, (CraftanGameActionEvent<*,*,*>, TurnState, List<TurnState>, Any?) -> TurnState?>> = mutableMapOf()
) {
    fun put(from: TurnState, to: List<TurnState>, chooser: (CraftanGameActionEvent<*,*,*>, TurnState, List<TurnState>, Any?) -> TurnState?) {
        if (adjacency.containsKey(from)) {
            throw IllegalArgumentException("Node $from already exists")
        }

        adjacency[from] = Pair(to, chooser)
    }

    fun neighbors(node: TurnState): List<TurnState> =
        adjacency[node]?.first ?: emptyList()

    fun getChooser(node: TurnState): (CraftanGameActionEvent<*,*,*>, TurnState, List<TurnState>, Any?) -> TurnState? {
        return adjacency[node]!!.second
    }
}