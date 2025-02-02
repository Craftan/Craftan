package de.craftan.engine.structures

import de.craftan.engine.CraftanResource
import de.craftan.engine.map.CraftanMap
import de.craftan.engine.map.GameTile
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.Direction
import de.craftan.engine.resources.WoodResource

class Road : CraftanStructure {
    override val cost: Map<CraftanResource, Int> = mapOf((WoodResource to 2))

    override fun canPlace(tile: TileCoordinate, direction: Direction, map: CraftanMap): Boolean {
        return map.coordinatesToTile[tile]!!.edges[direction]!!.structureInfo.structure == null
    }
}
