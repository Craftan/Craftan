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
    val allowedStructure: CraftanStructure,
    val player: CraftanPlayer,
    override var result: PlacedStructureEvent? = null,
    ) : CraftanGameAction<PlacedStructureEvent> {
    override fun <T : CraftanActionData> invoke(
        player: CraftanPlayer,
        data: T,
    ): Boolean {
        val data = data as PlacedStructureEventData
        if (data.structureInfo::class != allowedStructure::class) return false
        val tile = game.map.coordinatesToTile[data.coordinates] ?: return false
        if (tile.nodes[data.direction] == null) return false
        val canPlace = data.structureInfo.canPlace(data.coordinates, data.direction, game.map)
        if (!canPlace) return false
        val playerHasRessources = player.inventory.containsAtleastOne(data.structureInfo.cost)
        if (!playerHasRessources) return  false
        val playerHasStructure = player.inventory.containsAtleastOne(data.structureInfo)
        if (!playerHasStructure) return  false

        result =
            eventBus.fire(
                PlacedStructureEvent(
                    game,
                    player,
                    data.coordinates,
                    data.direction,
                    data.structureInfo,
                ),
            )
        return true
    }

    override fun asItem(): CraftanActionItem<PlacedStructureEvent> = craftanActionItem(Material.WHITE_CONCRETE, 1, player, this) {}
}

data class PlacedStructureEventData(
    val coordinates: TileCoordinate,
    val direction: Direction,
    val structureInfo: CraftanStructure,
) : CraftanActionData
