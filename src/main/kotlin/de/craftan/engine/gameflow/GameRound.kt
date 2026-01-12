package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.flows.TurnFlow
import net.ormr.eventbus.EventBus

/**
 * Models a round of a CraftanGame
 */
abstract class GameRound(
    val eventBus: EventBus<Any, CraftanGameEvent>
) {
    /**
     * Abstract name to be rendered ingame
     */
    abstract val name: String

    var turnIndex: Int = 0

    abstract val turnSequence: MutableList<Pair<CraftanPlayer, TurnFlow>>

    fun currentTurn(): Pair<CraftanPlayer, TurnFlow> = turnSequence[turnIndex]

    // TODO should listen to turn end event and then call this
    fun nextTurn() {
        //println("Current turn: ${currentTurn().first.name} to next turn: ${turnSequence[turnIndex + 1].first.name}")
        turnIndex++
        if (turnIndex >= turnSequence.size) {
            finish()
            return
        }
        // TODO Start the turn of the player
    }

    fun finish() {
        println("Round $name finished!")
        //eventBus.unsubscribe(this)
        // TODO Fire finish round event
    }
}