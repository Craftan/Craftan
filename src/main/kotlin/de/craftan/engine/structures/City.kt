package de.craftan.engine.structures

import de.craftan.engine.CraftanResource
import de.craftan.engine.map.GameTile
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.Direction
import de.craftan.engine.resources.WoodResource

class City : CraftanStructure {
    override val cost: Map<CraftanResource, Int> = mapOf((WoodResource to 2))
    override fun canPlace(tile: TileCoordinate, direction: Direction, map: Map<TileCoordinate, GameTile>): Boolean {
        TODO("Not yet implemented")
    }
}
