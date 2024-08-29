package de.craftan.engine

/**
 * Models the sequence the players play in
 * @param players An list ordered by the turn order
 * @see CraftanGame
 */
class TurnSequence(
    private val players: List<CraftanPlayer>,
) {
    private var currentPlayerIndex: Int = 0

    /**
     * Returns the next player to have his turn
     */
    fun getNextPlayer(): CraftanPlayer {
        if (currentPlayerIndex < players.size - 1) {
            currentPlayerIndex++
            return players[currentPlayerIndex]
        }
        currentPlayerIndex = 0
        return players[0]
    }
}
