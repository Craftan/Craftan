package de.craftan.engine

import de.craftan.engine.map.CraftanMap
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.CraftanDirection
import de.craftan.engine.resources.CraftanActionCard
import de.craftan.engine.resources.CraftanResourceType
import de.craftan.engine.structures.CraftanStructure

class CraftanGameStateHandler(
    val map: CraftanMap,
    initialResources: Map<CraftanPlayer, MutableMap<CraftanResourceType, Int>>,
    initialCards: Map<CraftanPlayer, Map<CraftanActionCard, Int>>,
    initialWinPoints: Map<CraftanPlayer, Int>
) {

    val state: CraftanGameState = CraftanGameState(
        map, initialResources, initialCards, initialWinPoints
    )

    fun hasResources(
        player: CraftanPlayer,
        resources: Map<CraftanResourceType, Int>
    ): Boolean {
        return resources.all { (resourceType, amount) ->
            (state.resources[player]?.get(resourceType)?: throw IllegalArgumentException("Player or Resource type not found")) >= amount
        }
    }

    fun placeStructure(
        player: CraftanPlayer,
        structure: CraftanStructure,
        tileCoordinate: TileCoordinate,
        direction: CraftanDirection,
        requireResources: Boolean
    ) {
        if (requireResources) removeResources(player, structure.cost)
        map.placeStructure(tileCoordinate, structure, direction, player)
    }

    fun removeResources(
        player: CraftanPlayer,
        resources: Map<CraftanResourceType, Int>
    ) {
        resources.forEach { (resourceType, amount) ->
            val resources = state.resources[player]?: throw IllegalArgumentException("Player not found")
            val initialAmount = resources[resourceType]
            val newAmount = initialAmount?.minus(amount) ?: throw IllegalArgumentException("Resource type not found")
            resources[resourceType] = newAmount
        }
    }
}

data class CraftanGameState(
    val map: CraftanMap,
    val resources: Map<CraftanPlayer, MutableMap<CraftanResourceType, Int>>,
    val cards: Map<CraftanPlayer, Map<CraftanActionCard, Int>>,
    val winPoints: Map<CraftanPlayer, Int>
)