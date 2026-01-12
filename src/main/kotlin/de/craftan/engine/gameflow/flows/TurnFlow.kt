package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.gameflow.CraftanGameTurnEndEvent
import de.craftan.engine.gameflow.TurnState
import de.craftan.engine.gameflow.action.CraftanGameActionEvent
import net.ormr.eventbus.EventBus

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
) {
    val eventBus: EventBus<Any, CraftanGameEvent> = EventBus()

    /**
     * Shows the current state of this flow
     */
    var state = startState
        private set

    val graph = Graph()

    fun putStateTransition(from: TurnState, to: MutableList<TurnState>, chooser: (CraftanGameEvent) -> TurnState?) {
        graph.put(from, to, chooser)
    }

    init {
        eventBus.on<CraftanGameActionEvent<*,*>> {
            nextState(this)
        }
    }

    /**
     * Goes to the next state of this flow, or finishes the flow if the last state was reached
     */
    // Listen to a Endstate eventbus
    fun nextState(event: CraftanGameEvent) {
        // TODO This means the state shouldn't change just yet
        val nextState = graph.getChooser(state).invoke(event) ?: return
        val neighbors = graph.neighbors(state)
        if (neighbors.isEmpty()) {
            finishFlow()
            return
        }
        state = nextState
    }

    /**
     * Finished the flow by notifying the game
     */
    open fun finishFlow() {
        // Basically fire an Event
        eventBus.fire(CraftanGameTurnEndEvent())
    }
}

data class Graph(
    val adjacency: MutableMap<TurnState, Pair<MutableList<TurnState>, (CraftanGameEvent) -> TurnState?>> = mutableMapOf()
) {
    fun put(from: TurnState, to: MutableList<TurnState>, chooser: (CraftanGameEvent) -> TurnState?) {
        if (adjacency.containsKey(from)) {
            throw IllegalArgumentException("Node $from already exists")
        }

        adjacency[from] = Pair(to, chooser)
    }

    fun neighbors(node: TurnState): List<TurnState> =
        adjacency[node]?.first ?: emptyList()

    fun getChooser(node: TurnState):(CraftanGameEvent) -> TurnState? {
        return adjacency[node]!!.second
    }
}