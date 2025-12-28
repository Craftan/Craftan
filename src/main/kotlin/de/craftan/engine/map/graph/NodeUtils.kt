package de.craftan.engine.map.graph

import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.TileDirection

/**
 * Generates empty nodes for all tiles
 */
fun generateNodes(tiles: Set<TileCoordinate>): MutableMap<TileCoordinate, MutableMap<NodeDirection, Node>> {
    val cordToNodeMap = mutableMapOf<TileCoordinate, MutableMap<NodeDirection, Node>>()
    tiles.forEach { cordToNodeMap[it] = mutableMapOf() }

    for (tile in tiles) {
        for (nodeDirection in NodeDirection.entries) {
            if (!cordToNodeMap[tile]!!.contains(nodeDirection)) {
                val node = Node()
                node.tiles.add(tile)
                cordToNodeMap[tile]!![nodeDirection] = node
                appendNodeToNeigbours(tile, node, tiles, nodeDirection, cordToNodeMap)
            }
        }
    }
    return cordToNodeMap
}

private fun appendNodeToNeigbours(
    tile: TileCoordinate,
    node: Node,
    tiles: Set<TileCoordinate>,
    nodeDirection: NodeDirection,
    cordToNodeMap: MutableMap<TileCoordinate, MutableMap<NodeDirection, Node>>,
) {
    val neighbours = getNeighbours(tile, tiles, nodeDirection)
    for (neighbour in neighbours) {
        val neighbourDirection = TileDirection.entries.find { it.tileCoordinate == neighbour - tile }
        val neighbourNodeDirection = neighborsNodeDirection[nodeDirection]!![neighbourDirection]!!
        cordToNodeMap[neighbour]!![neighbourNodeDirection] = node
    }
}

private fun getNeighbours(
    tile: TileCoordinate,
    tiles: Set<TileCoordinate>,
    nodeDirection: NodeDirection,
): Set<TileCoordinate> {
    val neighbours = mutableSetOf<TileCoordinate>()
    for (tileDirection in nodeDirection.otherTileDirections.toList()) {
        val neighbour = tiles.find { it == tile + tileDirection.tileCoordinate }
        if (neighbour != null) {
            neighbours.add(neighbour)
        }
    }
    return neighbours
}

/**
 * For a node in the north direction of a given tile this tells what direction the node has for the bordering tiles.
 * So for the Tile in the north-west direction it would be in the south-east
 */
val neighborsNodeDirection =
    mapOf(
        Pair(
            NodeDirection.NORTH,
            mapOf(
                Pair(TileDirection.NORTH_WEST, NodeDirection.SOUTH_EAST),
                Pair(TileDirection.NORTH_EAST, NodeDirection.SOUTH_WEST),
            ),
        ),
        Pair(
            NodeDirection.NORTH_EAST,
            mapOf(
                Pair(TileDirection.NORTH_EAST, NodeDirection.SOUTH),
                Pair(TileDirection.EAST, NodeDirection.NORTH_WEST),
            ),
        ),
        Pair(
            NodeDirection.SOUTH_EAST,
            mapOf(
                Pair(TileDirection.EAST, NodeDirection.SOUTH_WEST),
                Pair(TileDirection.SOUTH_EAST, NodeDirection.NORTH),
            ),
        ),
        Pair(
            NodeDirection.SOUTH,
            mapOf(
                Pair(TileDirection.SOUTH_EAST, NodeDirection.NORTH_WEST),
                Pair(TileDirection.SOUTH_WEST, NodeDirection.NORTH_EAST),
            ),
        ),
        Pair(
            NodeDirection.SOUTH_WEST,
            mapOf(
                Pair(TileDirection.SOUTH_WEST, NodeDirection.NORTH),
                Pair(TileDirection.WEST, NodeDirection.SOUTH_EAST),
            ),
        ),
        Pair(
            NodeDirection.NORTH_WEST,
            mapOf(
                Pair(TileDirection.WEST, NodeDirection.NORTH_EAST),
                Pair(TileDirection.NORTH_WEST, NodeDirection.SOUTH),
            ),
        ),
    )
