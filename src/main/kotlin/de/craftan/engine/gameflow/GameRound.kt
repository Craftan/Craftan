package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGame
import de.craftan.bridge.CraftanBridgePlayer
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.flows.TurnFlow

/**
 * Models a round of a CraftanGame
 */
abstract class GameRound {
    /**
     * Abstract name to be rendered ingame
     */
    abstract val name: String
    
    var turnIndex: Int = 0

    abstract val turnSequence: MutableList<Pair<CraftanPlayer, TurnFlow>>

    fun currentTurn(): Pair<CraftanPlayer, TurnFlow> = turnSequence[turnIndex]

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
        // Fire finish round event
    }
}
