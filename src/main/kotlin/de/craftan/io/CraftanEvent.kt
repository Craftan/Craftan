package de.craftan.io

import de.craftan.Craftan
import net.ormr.eventbus.CancellableEvent
import net.ormr.eventbus.Event
import net.ormr.eventbus.EventBus

typealias CraftanEvent = Event
typealias CraftanEventBus = EventBus<Any, CraftanEvent>

val globalEventBus = Craftan.eventBus

abstract class CancellableCraftanEvent: CraftanEvent, CancellableEvent {
    override var isCancelled: Boolean = false

    override fun cancel() {
        isCancelled = true
    }
}