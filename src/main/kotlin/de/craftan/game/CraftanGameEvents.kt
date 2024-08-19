package de.craftan.game

import de.craftan.events.GenericEventHandler

/**
 * Includes all the events a craftan game has
 * @see CraftanGame
 */
abstract class CraftanGameEvents : GenericEventHandler<CraftanGame>() {
    fun onStart(block: CraftanGame.() -> Unit) = registerEvent(CraftanEvent.GAME_START.key, block)

    fun onEnd(block: CraftanGame.() -> Unit) = registerEvent(CraftanEvent.GAME_END.key, block)
}

enum class CraftanEvent(
    val key: String,
) {
    GAME_START("start"),
    GAME_END("end"),
}
