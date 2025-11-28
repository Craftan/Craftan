package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.flows.TurnFlow

/**
 * Models a round of a CraftanGame
 */
interface GameRound {
    /**
     * Abstract name to be rendered ingame
     */
    val name: String

    /**
     * The number of round from the beginning of the game
     */
    val roundNumber: Int
    
    var turnIndex: Int

    val turnSequence:List<Pair<CraftanPlayer, TurnFlow>>

    /**
     * The game the round is inside of
     */
    val game: CraftanGame

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
