package de.craftan.engine

/**
 * Models a round of a CraftanGame
 */
interface GameRound {
    /**
     * The number of round from the beginning of the game
     */
    val index: Int

    /**
     * The player which turn it is
     */
    val player: CraftanPlayer

    /**
     * the flow of this round
     */
    val flow: RoundFlow
}
