package de.craftan.engine

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
    val index: Int

    /**
     * the flow of this round
     */
    val flow: RoundFlow

    /**
     * The game the round is inside of
     */
    val game: CraftanGame

    /**
     * Used before this round actually starts
     */
    fun prepare() {}
}

/**
 * Models a global round, which takes all players into consideration, and each player can make their turn
 */
interface GlobalGameRound : GameRound

/**
 * A round which is focused around a single player
 */
interface PlayerGameRound : GameRound {
    /**
     * The player to focus
     */
    val player: CraftanPlayer
}
