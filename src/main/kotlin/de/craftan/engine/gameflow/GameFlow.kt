package de.craftan.engine.gameflow

import de.craftan.engine.CraftanPlayer

abstract class GameFlow {

    var roundIndex = 0

    /**
     * The current round of the game
     */
    abstract var round: GameRound

    /**
     * All participating players
     */
    abstract val players: List<CraftanPlayer>

    /**
     * Starts the next round
     */
    abstract fun nextRound()

    /**
     * Sequence of players in the rounds
     * e.g. player2 -> player1 -> player4 -> player3 -> loop
     */
    lateinit var playerSequence: TurnSequence

}