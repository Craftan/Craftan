package de.craftan.engine

/**
 * Models the sequence the players play in
 * @param players A list ordered by the turn order
 * @see CraftanGame
 */
class TurnSequence(
    private val players: List<CraftanPlayer>,
) {
    private var currentPlayerIndex: Int = -1

    /**
     * Returns the next player to have his turn. Starting with the first one
     */
    fun getNextPlayer(): CraftanPlayer {
        currentPlayerIndex++
        if (currentPlayerIndex > players.size - 1) {
            currentPlayerIndex = 0
        }

        return players[currentPlayerIndex]
    }
}
