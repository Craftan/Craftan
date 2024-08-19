package de.craftan.game

import de.staticred.kia.inventory.item.KItem

/**
 * Models a game and the current game state inside a Craftan lobby
 */
abstract class CraftanGame : CraftanGameEvents() {
    /**
     * The config of this game
     */
    abstract val config: CraftanGameConfig

    /**
     * The current round of the game
     */
    abstract val round: CraftanGameRound

    /**
     * All participating players
     */
    abstract val players: List<CraftanPlayer>

    /**
     * The state the game is at currently
     */
    abstract val state: CraftanGameState

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
     * The current state the game is at
     */
    val state: CraftanRoundState
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
     */
    val name: String

    /**
     * The current game
     */
    val game: CraftanGame

    /**
     * A list of all possible actions for a player
     */
    val actions: List<CraftanGameAction>

    /**
     * Gets the next state of the game
     * @return next state
     */
    fun nextState(): CraftanRoundState

    /**
     * Checks if the current state can proceed to the next state
     * @return true if so
     */
    fun canProceed(): Boolean
}

/**
 * Models an interaction between the player and the game
 */
interface CraftanGameAction {
    val game: CraftanGame

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
    fun asItem(): KItem
}

enum class CraftanGameState {
    /**
     * Game is in lobby and waiting to be started
     */
    WAITING,

    /**
     * Game is currently running
     */
    RUNNING,

    /**
     * Game is finished, either by a leaver, or somebody won
     */
    FINISHED,
}
