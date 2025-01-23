package de.craftan.engine.gameflow.actions

import de.craftan.engine.CraftanActionData
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameAction
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.CraftanActionItem
import de.craftan.engine.gameflow.craftanActionItem
import de.craftan.engine.gameflow.events.actions.PlacedStructureEvent
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.Direction
import de.craftan.engine.map.graph.NodeDirection
import de.craftan.engine.structures.City
import de.craftan.engine.structures.CraftanStructure
import org.bukkit.Material

class PlaceStructureAction(
    override val game: CraftanGame,
    override var result: PlacedStructureEvent?,
    val player: CraftanPlayer,
) : CraftanGameAction<PlacedStructureEvent> {
    override fun <T : CraftanActionData> invoke(
        player: CraftanPlayer,
        data: T,
    ): Boolean {
        val data = data as PlacedStructureEventData
        val result =
            eventBus.fire(
                PlacedStructureEvent(
                    game,
                    player,
                    data.coordinates,
                    data.direction,
                    data.structureInfo,
                ),
            )
        if (result.isCancelled) return true
        if (data.direction is NodeDirection) {
            val tile = game.map.coordinatesToTile[data.coordinates]
            if (tile != null && tile.nodes[data.direction] != null) {
                val node = tile.nodes[data.direction]!!
                if (node.structureInfo.structure is City) return false
                val playerHasRessources = player.inventory.containsAtleastOne(data.structureInfo.cost)
                val playerHasStructure = player.inventory.containsAtleastOne(data.structureInfo)
                if (playerHasRessources && playerHasStructure) {
                    player.inventory.remove(data.structureInfo.cost)
                    player.inventory.remove(data.structureInfo)
                }
                val canPlace = data.structureInfo.canPlace(data.coordinates, data.direction, game.map.coordinatesToTile)
                if (!canPlace) return false
                node.structureInfo.structure = data.structureInfo
            } else {
                return false
            }
        } else {
            TODO()
        }
        return true
    }

    override fun asItem(): CraftanActionItem<PlacedStructureEvent> = craftanActionItem(Material.WHITE_CONCRETE, 1, player, this) {}
}

data class PlacedStructureEventData(
    val coordinates: TileCoordinate,
    val direction: Direction,
    val structureInfo: CraftanStructure,
) : CraftanActionData
