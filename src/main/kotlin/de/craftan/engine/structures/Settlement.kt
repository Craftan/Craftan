package de.craftan.engine.structures

import de.craftan.engine.CraftanResource
import de.craftan.engine.map.CraftanMapUtils
import de.craftan.engine.map.GameTile
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.Direction
import de.craftan.engine.resources.WoodResource

class Settlement : CraftanStructure {
    override val cost: Map<CraftanResource, Int> = mapOf((WoodResource to 2))

    override fun canPlace(
        tile: TileCoordinate,
        direction: Direction,
        map: Map<TileCoordinate, GameTile>,
    ): Boolean {
        if (map[tile]!!.nodes[direction]!!.structureInfo.structure != null) return false
        if (!CraftanMapUtils.distanceBetweenStructuresAtleast(tile, direction, map, 1, Settlement::class)) return false
        return true
    }
}
