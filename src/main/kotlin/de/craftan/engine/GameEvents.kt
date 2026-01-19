package de.craftan.engine

import de.craftan.engine.gameflow.action.CraftanActionData
import de.craftan.engine.gameflow.action.CraftanGameAction
import net.ormr.eventbus.CancellableEvent
import net.ormr.eventbus.Event
import kotlin.reflect.KClass

/**
 * Models an event from a game instance
 * @param game the game
 */
abstract class CraftanGameEvent() : Event

class CraftanGameEndTurnEvent () : CraftanGameEvent()

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
