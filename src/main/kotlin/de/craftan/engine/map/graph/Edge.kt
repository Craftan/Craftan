package de.craftan.engine.map.graph

import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.TileDirection

/**
 * An edge connects the nodes and lies between up to 2 tiles
 */
data class Edge(
    val tiles: Set<TileCoordinate>,
    val nodes: Pair<Node, Node>,
    val structureInfo: StructureInfo,
)

enum class EdgeDirection(
    val nodeDirections: Pair<NodeDirection, NodeDirection>,
    val otherNode: TileDirection,
) : Direction {
    NORTH_WEST(Pair(NodeDirection.NORTH, NodeDirection.NORTH_WEST), TileDirection.NORTH_WEST),
    NORTH_EAST(Pair(NodeDirection.NORTH, NodeDirection.NORTH_EAST), TileDirection.NORTH_EAST),
    EAST(Pair(NodeDirection.NORTH_EAST, NodeDirection.SOUTH_EAST), TileDirection.EAST),
    SOUTH_EAST(Pair(NodeDirection.SOUTH, NodeDirection.NORTH_EAST), TileDirection.SOUTH_EAST),
    SOUTH_WEST(Pair(NodeDirection.SOUTH, NodeDirection.NORTH_WEST), TileDirection.SOUTH_WEST),
    WEST(Pair(NodeDirection.NORTH_WEST, NodeDirection.SOUTH_WEST), TileDirection.WEST),
}
