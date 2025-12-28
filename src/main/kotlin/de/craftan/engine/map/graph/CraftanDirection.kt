package de.craftan.engine.map.graph

import de.craftan.engine.map.TileDirection

interface CraftanDirection {
}

/**
 * The direction on a tile a node can be in.
 * Contains the direction of the tiles the node also borders if they exist.
 */
enum class NodeDirection(
    val otherTileDirections: Pair<TileDirection, TileDirection>,
): CraftanDirection {
    NORTH_WEST(Pair(TileDirection.WEST, TileDirection.NORTH_WEST)),
    NORTH(Pair(TileDirection.NORTH_WEST, TileDirection.NORTH_EAST)),
    NORTH_EAST(Pair(TileDirection.EAST, TileDirection.NORTH_EAST)),
    SOUTH_EAST(Pair(TileDirection.EAST, TileDirection.SOUTH_EAST)),
    SOUTH(Pair(TileDirection.SOUTH_WEST, TileDirection.SOUTH_EAST)),
    SOUTH_WEST(Pair(TileDirection.WEST, TileDirection.SOUTH_WEST)),
}

enum class EdgeDirection(
    val nodeDirections: Pair<NodeDirection, NodeDirection>,
    val otherNode: TileDirection,
): CraftanDirection {
    NORTH_WEST(Pair(NodeDirection.NORTH, NodeDirection.NORTH_WEST), TileDirection.NORTH_WEST),
    NORTH_EAST(Pair(NodeDirection.NORTH, NodeDirection.NORTH_EAST), TileDirection.NORTH_EAST),
    EAST(Pair(NodeDirection.NORTH_EAST, NodeDirection.SOUTH_EAST), TileDirection.EAST),
    SOUTH_EAST(Pair(NodeDirection.SOUTH, NodeDirection.NORTH_EAST), TileDirection.SOUTH_EAST),
    SOUTH_WEST(Pair(NodeDirection.SOUTH, NodeDirection.NORTH_WEST), TileDirection.SOUTH_WEST),
    WEST(Pair(NodeDirection.NORTH_WEST, NodeDirection.SOUTH_WEST), TileDirection.WEST),
}
