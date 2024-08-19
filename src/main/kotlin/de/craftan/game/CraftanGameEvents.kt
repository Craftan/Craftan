package de.craftan.game

import de.craftan.events.EventParameters

/**
 * Includes all the events a craftan game has
 * @see CraftanGame
 */
abstract class CraftanGameEvents {
    private val eventMap = mutableMapOf<CraftanEventEnum, MutableList<CraftanGame.(event: CraftanEvent<out EventParameters>) -> Unit>>()

    fun onStart(block: CraftanGame.(event: CraftanEvent<EventParameters>) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        addEventListener(block as CraftanGame.(CraftanEvent<out EventParameters>) -> Unit, CraftanEventEnum.GAME_START)
    }

    fun onEnd(block: CraftanGame.(event: CraftanEvent<EventParameters>) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        addEventListener(block as CraftanGame.(CraftanEvent<out EventParameters>) -> Unit, CraftanEventEnum.GAME_END)
    }

    private fun addEventListener(
        listener: CraftanGame.(event: CraftanEvent<out EventParameters>) -> Unit,
        event: CraftanEventEnum,
    ) {
        val list = eventMap[event]

        if (list == null) {
            eventMap[event] = mutableListOf()
        }

        eventMap[event]?.add(listener) ?: error("This should not be null lol")
    }

    fun triggerEvent(
        eventMapping: CraftanEventEnum,
        game: CraftanGame,
        parameters: CraftanEvent<out EventParameters>,
    ) {
        eventMap[eventMapping]?.forEach { it(game, parameters) }
    }
}

open class CraftanEvent<P : EventParameters>(
    val event: CraftanEventEnum,
)

data class GameStartParameters(
    val idk: Int,
) : EventParameters()

enum class CraftanEventEnum(
    val key: String,
) {
    GAME_START("start"),
    GAME_END("end"),
}
