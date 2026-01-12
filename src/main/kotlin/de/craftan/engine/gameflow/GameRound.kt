package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.flows.TurnFlow
import net.ormr.eventbus.EventBus

/**
 * Models a round of a CraftanGame
 */
abstract class GameRound {
    /**
     * Abstract name to be rendered ingame
     */
    abstract val name: String

    val eventBus: EventBus<Any, CraftanGameEvent> = EventBus()
    
    var turnIndex: Int = 0

    abstract val turnSequence: MutableList<Pair<CraftanPlayer, TurnFlow>>

    fun currentTurn(): Pair<CraftanPlayer, TurnFlow> = turnSequence[turnIndex]

    init {
        eventBus.on<CraftanGameTurnEndEvent> { nextTurn()}
    }

    // TODO should listen to turn end event and then call this
    fun nextTurn() {
        turnIndex++
        if (turnIndex >= turnSequence.size) {
            finish()
        }
        val (player, flow) = turnSequence[turnIndex]
        // TODO Start the turn of the player
    }

    fun finish() {
        // TODO Fire finish round event
    }
}

class CraftanGameTurnEndEvent(
    game: CraftanGame
) : CraftanGameEvent(game)
