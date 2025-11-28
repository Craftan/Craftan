package de.craftan.engine

import de.craftan.config.CraftanConfig
import de.craftan.engine.gameflow.CraftanActionItem
import de.staticred.kia.inventory.item.KItem
import net.ormr.eventbus.EventBus

/**
 * Models an interaction between the player and the game
 * @param R is the expected result of the action
 */
interface CraftanGameAction<R> {
    val eventBus: EventBus<Any, CraftanEvent>
        get() = EventBus()

    /**
     * This method has multiple responsibilities, it is the core business logic of the action.
     * Firstly it should communicate with the GameState via events to alter the state of the game.
     * Secondly it should communicate with the frontend to inform it about the "result" of the action.
     *      -> In essence like http status codes
     *
     * @param player the player who invoked this action
     * @return whether the action was successful or not
     */
    fun <T: CraftanActionData> invoke(player: CraftanPlayer, data: T): Boolean
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