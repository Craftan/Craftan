package de.craftan.engine.gameflow.action

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanGameStateHandler
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameFlow
import kotlin.reflect.KClass

/**
 * Models an interaction between the player and the game
 */
interface CraftanGameAction<T: CraftanActionData, R> {
    /**
     * This method has multiple responsibilities, it is the core business logic of the action.
     * Firstly it should communicate with the GameState via events to alter the state of the game.
     * Secondly it should communicate with the frontend to inform it about the "result" of the action.
     *      -> In essence like http status codes
     *
     * @param player the player who invoked this action
     * @return whether the action was successful or not
     */
    fun invoke(player: CraftanPlayer, data: T, stateHandler: CraftanGameStateHandler): R

    fun verify(player: CraftanPlayer, data: T, stateHandler: CraftanGameStateHandler, gameFlow: GameFlow): VerificationResult
}

data class VerificationResult(
    val success: Boolean,
    val reason: String?
)

class CraftanGameActionEvent<T: CraftanActionData, A: CraftanGameAction<T, R>, R> (
    val game: CraftanGame,
    val actionData: T,
    val player: CraftanPlayer,
    val actionClass: KClass<A>
) : CraftanGameEvent() {

    fun verifyAndInvoke(): R {
        val currentTurnPair = game.gameFlow.round?.currentTurn()
            ?: throw IllegalStateException("Action $actionClass invoked, but no active round or turn found!")

        // Verify it is actually this player's turn
        if (currentTurnPair.first != player) {
            throw IllegalStateException("Action $actionClass invoked, but it is not this player's turn!")
        }

        val allActions = currentTurnPair.second.state.actions

        for (action in allActions) {
            if (action::class == actionClass) {
                // Safe cast to the specific action type to satisfy generic T
                @Suppress("UNCHECKED_CAST")
                val typedAction = action as CraftanGameAction<T, R>

                val verificationResult = typedAction.verify(player, actionData, game.stateHandler, game.gameFlow)
                if (!verificationResult.success) {
                    // TODO Communicate the failure to the frontend
                    throw IllegalStateException("Action $actionClass failed verification! Reason: ${verificationResult.reason}")
                }
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