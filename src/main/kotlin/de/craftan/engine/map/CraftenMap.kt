package de.craftan.engine.map

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.map.graph.CraftanDirection
import de.craftan.engine.map.graph.EdgeDirection
import de.craftan.engine.map.graph.NodeDirection
import de.craftan.engine.map.graph.StructureInfo
import de.craftan.engine.structures.CraftanStructure

class CraftanMap(
    val tiles: MutableMap<TileCoordinate, GameTile>
) {
    fun isEmpty(
        tileCoordinate: TileCoordinate,
        direction: CraftanDirection,
    ): Boolean {
        if (direction is NodeDirection) {
            return tiles[tileCoordinate]?.nodes?.get(direction)?.structureInfo == null
        } else if (direction is EdgeDirection) {
            return tiles[tileCoordinate]?.edges?.get(direction)?.structureInfo == null
        }
        throw IllegalArgumentException("Direction must be of type CraftanDirection")
    }

    fun placeStructure(
        tileCoordinate: TileCoordinate,
        structure: CraftanStructure,
        direction: CraftanDirection,
        owner: CraftanPlayer
    ) {
        val tile = tiles[tileCoordinate]?: throw IllegalArgumentException("Tile not found")
        if (direction is NodeDirection) {
            val node = tile.nodes[direction] ?: throw IllegalArgumentException("Node not found")
            node.structureInfo = StructureInfo(structure, owner)
        } else if (direction is EdgeDirection) {
            val edge = tile.edges[direction] ?: throw IllegalArgumentException("Edge not found")
            edge.structureInfo = StructureInfo(structure, owner)
        } else {
            throw IllegalArgumentException("Direction must be of type CraftanDirection")
        }
    }
}