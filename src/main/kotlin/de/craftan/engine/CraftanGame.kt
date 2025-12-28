package de.craftan.engine

import de.craftan.engine.gameflow.GameFlow
import net.ormr.eventbus.EventBus

/**
 * Models a game and the current game state inside a Craftan lobby
 */
abstract class CraftanGame(
    open val config: CraftanGameConfig,
) {
    val eventBus: EventBus<Any, CraftanGameEvent> = EventBus()

    abstract val gameFlow: GameFlow

    abstract val stateHandler: CraftanGameStateHandler

    /**
     * Starts the game
     */
    abstract fun start()
}
