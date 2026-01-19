package de.craftan.engine

import de.craftan.engine.map.CraftanMap
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.CraftanDirection
import de.craftan.engine.resources.CraftanActionCard
import de.craftan.engine.resources.CraftanResourceType
import de.craftan.engine.structures.CraftanStructure
import de.craftan.engine.structures.Settlement

class CraftanGameStateHandler(
    val map: CraftanMap,
    initialResources: Map<CraftanPlayer, MutableMap<CraftanResourceType, Int>>,
    initialCards: Map<CraftanPlayer, Map<CraftanActionCard, Int>>,
    initialWinPoints: Map<CraftanPlayer, Int>
) {

    val state: CraftanGameState = CraftanGameState(
        map, initialResources, initialCards, initialWinPoints
    )

    fun distributeResources(rolledNumber: Int) {
        val relevantTiles = map.tiles.values.filter { it.tileInfo.chance.value == rolledNumber }
        for (tile in relevantTiles) {
            val resource = tile.tileInfo.type.resourceType ?: continue
            for (node in tile.nodes.values) {
                val info = node.structureInfo ?: continue
                when(info.structure) {
                    is Settlement -> addResourcesToPlayer(info.owner, mapOf(resource to 1))
                }
            }
        }
    }

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

    fun addResourcesToPlayer(player: CraftanPlayer, resources: Map<CraftanResourceType, Int>) {
        resources.forEach { (resourceType, amount) ->
            val resources = state.resources[player]?: throw IllegalArgumentException("Player not found")
            resources[resourceType] = (resources[resourceType]?: throw IllegalStateException("Resource type not found")) + amount
        }
    }
}

data class CraftanGameState(
    val map: CraftanMap,
    val resources: Map<CraftanPlayer, MutableMap<CraftanResourceType, Int>>,
    val cards: Map<CraftanPlayer, Map<CraftanActionCard, Int>>,
    val winPoints: Map<CraftanPlayer, Int>
)