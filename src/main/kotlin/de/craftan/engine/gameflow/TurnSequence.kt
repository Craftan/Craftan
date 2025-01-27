package de.craftan.engine.gameflow

import de.craftan.engine.CraftanPlayer

/**
 * Models the sequence the players play in
 * @param players A list ordered by the turn order
 * @see CraftanGame
 */
class TurnSequence(
    private val players: List<CraftanPlayer>,
) {
    private var currentPlayerIndex: Int = 0

    var currentPlayer: CraftanPlayer = players[currentPlayerIndex]

    /**
     * Gives the Turn-over to the next player
     */
    fun nextPlayer() {
        currentPlayerIndex++
        if (currentPlayerIndex > players.size - 1) {
            currentPlayerIndex = 0
        }
        currentPlayer = players[currentPlayerIndex]
    }

    /**
     * Gives the Turn-over to the previous player
     */
    fun previousPlayer() {
        currentPlayerIndex--
        if (currentPlayerIndex < 0) {
            currentPlayerIndex = players.size - 1
        }
        currentPlayer = players[currentPlayerIndex]
    }

    fun getPlayerAmount():Int {
        return players.size
    }
}
