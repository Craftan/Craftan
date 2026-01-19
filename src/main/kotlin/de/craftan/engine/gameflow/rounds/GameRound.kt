package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameFlow
import de.craftan.engine.gameflow.flows.TurnFlow

/**
 * Models a round of a CraftanGame
 */
abstract class GameRound(
    val flow: GameFlow
) {
    /**
     * Abstract name to be rendered ingame
     */
    abstract val name: String

    var turnIndex: Int = 0

    abstract val turnSequence: MutableList<Pair<CraftanPlayer, TurnFlow>>

    fun currentTurn(): Pair<CraftanPlayer, TurnFlow> = turnSequence[turnIndex]

    fun currentPlayer(): CraftanPlayer = currentTurn().first
    fun currentFlow(): TurnFlow = currentTurn().second
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
        flow.nextRound()
    }
}