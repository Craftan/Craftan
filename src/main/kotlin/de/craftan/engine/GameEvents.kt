package de.craftan.engine

import net.ormr.eventbus.CancellableEvent
import net.ormr.eventbus.Event

/**
 * Models an event from a game instance
 * @param game the game
 */
abstract class CraftanGameEvent() : Event

/**
 * Models an event which can be cancelled from a game isntance
 * @param game the game
 */
abstract class CancelableCraftanGameEvent(
) : CraftanGameEvent(),
    CancellableEvent {
    override var isCancelled: Boolean = false

    override fun cancel() {
        isCancelled = true
    }
}
