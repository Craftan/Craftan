package de.craftan.engine

import de.craftan.bridge.map.CraftanMap
import net.ormr.eventbus.Event
import net.ormr.eventbus.EventBus

abstract class CraftanGameState(
    val game: CraftanGame,
) {
    // The Map should not be in Bridge yo
    val map: CraftanMap? = null

    val ressources: Map<CraftanPlayer, Map<CraftanResource, Int>> = emptyMap() // innit gameressources with some like registered ressources in the config or sth like dat

    val cards: Map<CraftanPlayer, Map<CraftanActionCard, Int>> = emptyMap()

    val winPoints: Map<CraftanPlayer, Int> = emptyMap()

    // react to events here that should like alter these things DIRECTLY
    val eventBus: EventBus<Any, CraftanEvent> = EventBus()
}

abstract class CraftanGameStateEvent(
    game: CraftanGame
) : CraftanEvent(game)

// Register the events here
