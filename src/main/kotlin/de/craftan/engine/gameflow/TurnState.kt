package de.craftan.engine.gameflow

import de.craftan.engine.gameflow.action.CraftanActionData
import de.craftan.engine.gameflow.action.CraftanGameAction

/**
 * Models the current state a round is at
 * Models an abstract of what state the game will move into, after the current one, and modify the CraftanGame accordingly
 *
 * GameStates conduct actions, which can be executed by the player
 */
abstract class TurnState {
    /**
     * Abstract name of the state
     *
     * Will be shown ingame
     */
    abstract val name: String

    /**
     * A list of all possible actions for a player
     */
    abstract val actions: List<CraftanGameAction<out CraftanActionData, *>>
}
