package de.craftan

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer

/**
 * Models the sequence the players play in
 * @see CraftanGame
 */
class TurnSequence(
    var players: List<CraftanPlayer>,
) {
    var currentPlayerIndex: Int = 0

    fun getNextPlayer(): CraftanPlayer {
        if (currentPlayerIndex < players.size - 1) {
            currentPlayerIndex++
            return players[currentPlayerIndex]
        }
        return players[0]
    }
}
