package de.craftan

import de.craftan.engine.CraftanActionItem
import de.craftan.engine.CraftanEvent
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
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
