package de.craftan.engine.gameflow.action

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanGameStateHandler
import de.craftan.engine.CraftanPlayer
import kotlin.reflect.KClass

/**
 * Models an interaction between the player and the game
 */
interface CraftanGameAction<T: CraftanActionData> {
    /**
     * This method has multiple responsibilities, it is the core business logic of the action.
     * Firstly it should communicate with the GameState via events to alter the state of the game.
     * Secondly it should communicate with the frontend to inform it about the "result" of the action.
     *      -> In essence like http status codes
     *
     * @param player the player who invoked this action
     * @return whether the action was successful or not
     */
    fun invoke(player: CraftanPlayer, data: T, stateHandler: CraftanGameStateHandler): Boolean

    fun verify(player: CraftanPlayer, data: T, stateHandler: CraftanGameStateHandler): Boolean
}

class CraftanGameActionEvent<T: CraftanActionData, A: CraftanGameAction<T>> (
    game: CraftanGame,
    val actionData: T,
    val player: CraftanPlayer,
    val actionClass: KClass<A>
) : CraftanGameEvent(game) {
    fun verifyAndInvoke(): Boolean {
        val currentTurnPair = game.gameFlow.round?.currentTurn() 
            ?: throw IllegalStateException("Action $actionClass invoked, but no active round or turn found!")

        // Verify it is actually this player's turn
        if (currentTurnPair.first != player) return false

        val allActions = currentTurnPair.second.state.actions

        for (action in allActions) {
            if (action::class == actionClass) {
                // Safe cast to the specific action type to satisfy generic T
                @Suppress("UNCHECKED_CAST")
                val typedAction = action as CraftanGameAction<T>
                
                if (!typedAction.verify(player, actionData, game.stateHandler)) return false
                return typedAction.invoke(player, actionData, game.stateHandler)
            }
        }
        throw IllegalStateException("Action $actionClass not found in current turn state!")
    }
}

/**
 * Models a data object to be shared between the ingame and the engine
 */
interface CraftanActionData {
    companion object {
        fun empty(): CraftanActionData {
            return EmptyCraftanActionData()
        }
    }
}

class EmptyCraftanActionData: CraftanActionData