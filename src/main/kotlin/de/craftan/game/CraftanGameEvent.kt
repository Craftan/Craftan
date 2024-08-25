package de.craftan.game

import net.ormr.eventbus.CancellableEvent
import net.ormr.eventbus.Event

abstract class CraftanEvent(
    open val game: CraftanGame,
) : Event

abstract class CancelableGameEvent(
    override val game: CraftanGame,
) : CraftanEvent(game),
    CancellableEvent {
    override var isCancelled: Boolean = false

    override fun cancel() {
        isCancelled = true
    }
}
