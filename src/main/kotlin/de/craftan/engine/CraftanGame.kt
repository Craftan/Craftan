package de.craftan.engine

import de.craftan.bridge.lobby.CraftanSettings
import de.craftan.engine.gameflow.GameFlow
import net.ormr.eventbus.EventBus

/**
 * Models a game and the current game state inside a Craftan lobby
 */
abstract class CraftanGame(
    open val settings: CraftanSettings,
) {
    val eventBus: EventBus<Any, CraftanGameEvent> = EventBus()

    /**
     * The config of this game
     */
    abstract val config: CraftanGameConfig

    abstract val gameFlow: GameFlow

    /**
     * The state the game is at currently
     */
    abstract var state: CraftanGameState

    /**
     * Starts the game
     */
    abstract fun start()
}
