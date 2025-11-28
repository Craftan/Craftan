package de.craftan.engine

import net.ormr.eventbus.CancellableEvent
import net.ormr.eventbus.Event

/**
 * Models an event from a game instance
 * @param game the game
 */
abstract class CraftanEvent(
    open val game: CraftanGame,
) : Event
