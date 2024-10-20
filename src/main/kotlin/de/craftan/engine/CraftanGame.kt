package de.craftan.engine

import net.ormr.eventbus.EventBus

/**
 * Models a game and the current game state inside a Craftan lobby
 */
abstract class CraftanGame {
    val eventBus: EventBus<Any, CraftanEvent> = EventBus()

    /**
     * The config of this game
     */
    abstract val config: CraftanGameConfig

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
     * Sequence of players in the rounds
     * e.g. player2 -> player1 -> player4 -> player3 -> loop
     */
    lateinit var playerSequence: TurnSequence

    /**
     * The state the game is at currently
     */
    abstract var state: CraftanGameState

    /**
     * Starts the game to its pre phase
     */
    abstract fun startGame()

    /**
     * Starts the next round
     */
    abstract fun nextRound()

    /**
     * Starts the game
     */
    abstract fun start()
}
