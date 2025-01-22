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

    val game: CraftanGame

    /**
     * Outcome of the action
     * set by [invoke]
     */
    var result: R?

    /**
     * Executes this given action with the given player
     * Should fire an event for the game to handle the changed state
     *
     * @param player the player who invoked this action
     * @return whether the action was successful or not
     */
    fun invoke(player: CraftanPlayer, data: CraftanActionData): Boolean

    /**
     * Builds this action as a KItem
     * @see KItem
     * @return the item which can be placed inside the players inventory
     */
    fun asItem(): CraftanActionItem<R>
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