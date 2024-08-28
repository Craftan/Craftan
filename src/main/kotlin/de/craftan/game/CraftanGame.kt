package de.craftan.game

import de.staticred.kia.inventory.item.KItem
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
    abstract var round: CraftanGameRound

    /**
     * All participating players
     */
    abstract val players: List<CraftanPlayer>

    /**
     * Sequence of players in the rounds
     * e.g. player2 -> player1 -> player4 -> player3 -> loop
     */
    abstract val playerSequence: Sequence<CraftanPlayer>

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

/**
 * Models a round of a CraftanGame
 */
interface CraftanGameRound {
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
    val flow: CraftanRoundFlow
}

/**
 * Models the current state a round is at
 * Models an abstract of what state the game will move into, after the current one, and modify the CraftanGame accordingly
 *
 * GameStates conduct actions, which can be executed by the player
 *
 * @see CraftanGame
 */
interface CraftanRoundState {
    /**
     * Abstract name of the state
     *
     * Will be shown ingame
     */
    val name: String

    /**
     * The current game
     */
    val game: CraftanGame

    /**
     * A list of all possible actions for a player
     */
    val actions: List<CraftanGameAction<*>>

    /**
     * Called by the flow, when this state is now in the turn
     */
    fun setup()

    /**
     * Called by the flow, when this state is finished
     */
    fun cleanUp() {}

    /**
     * Called when this state is finished, and the game can move on
     */
    fun finish() {
        game.round.flow.nextState()
    }
}

/**
 * Models an interaction between the player and the game
 * @param R is the expected result of the action
 */
interface CraftanGameAction<R> {
    val game: CraftanGame

    /**
     * Outcome of the action
     * set by [invoke]
     */
    var result: R?

    /**
     * Executes this given action with the given player
     *
     * This should directly modify the supplied game object
     *
     * @param player the player who invoked this action
     * @return whether the action was successful or not
     */
    fun invoke(player: CraftanPlayer): Boolean

    /**
     * Builds this action as a KItem
     * @see KItem
     * @return the item which can be placed inside the players inventory
     */
    fun asItem(): CraftanActionItem<R>
}

enum class CraftanGameState {
    /**
     * Game is in lobby and waiting to be started
     */
    WAITING,

    /**
     * Before the actual game runs in its usual rounds, to determine the sequence of players
     */
    PRE_GAME,

    /**
     * Game is currently running
     */
    RUNNING,

    /**
     * Game is finished, either by a leaver, or somebody won
     */
    FINISHED,
}
