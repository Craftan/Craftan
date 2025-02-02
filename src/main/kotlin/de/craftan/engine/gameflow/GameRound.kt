package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer

/**
 * Models a round of a CraftanGame, which takes all players into consideration, and each player can make their turn
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

    /**
     * Used after a Flow is finished
     */
    fun finishFlow() {
        game.nextRound()
    }
}

/**
 * Models a global round, which takes all players into consideration, and each player can make their turn in order
 */
abstract class GlobalGameRound : GameRound {

    public var numberOfRepetitions: Int = 0

    override fun finishFlow() {
        if  (numberOfRepetitions < game.playerSequence.getPlayerAmount()) {
            numberOfRepetitions++
            game.playerSequence.nextPlayer()
            flow.repeat()
        } else {
            game.nextRound()
        }
    }
}
