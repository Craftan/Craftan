package de.craftan.engine.structures

import de.craftan.engine.CraftanResource
import de.craftan.engine.map.GameTile
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.Direction

interface CraftanStructure {
    /**
     * Cost to build this given structure
     */
    val cost: Map<CraftanResource, Int>

    fun canPlace(tile: TileCoordinate, direction: Direction, map:Map<TileCoordinate, GameTile>):Boolean
}
