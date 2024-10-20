package de.craftan.engine

import de.craftan.CraftanGameAction
import net.ormr.eventbus.EventBus

/**
 * Models the current state a round is at
 * Models an abstract of what state the game will move into, after the current one, and modify the CraftanGame accordingly
 *
 * GameStates conduct actions, which can be executed by the player
 *
 * @param game the game
 * @see CraftanGame
 */
abstract class RoundState(
    val game: CraftanGame,
) {
    val eventBus: EventBus<CraftanEvent, Any> = EventBus()

    /**
     * Abstract name of the state
     *
     * Will be shown ingame
     */
    abstract val name: String

    /**
     * The flow this state is used in
     */
    lateinit var flow: RoundFlow

    /**
     * A list of all possible actions for a player
     */
    abstract val actions: List<CraftanGameAction<*>>

    /**
     * time the player has to finish this state
     */
    val time = game.config.timeToRollDice

    /**
     * Handle what happens when the time ends
     * After this call, the game will proceed with the nex state
     */
    abstract fun onTimeEnd()

    /**
     * Called by the flow, when this state is now in the turn
     */
    abstract fun setup()

    /**
     * Called by the flow, when this state is finished
     */
    fun cleanUp() {}

    /**
     * Called when this state is finished, and the game can move on
     */
    fun finish() {
        game.round.flow.nextState()
    }
}
