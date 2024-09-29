package de.craftan.engine.map.graph

import de.craftan.engine.CraftanStructure
import de.craftan.engine.map.GameTile
import de.craftan.engine.map.TileDirection

/**
 * A Node is practically the place where the players can build settlements and cities.
 * It is the intersection of up to 3 tiles and is connected to other nodes via the edges connected to it
 */
data class Node(
    val structureInfo: StructureInfo,
    val tiles: MutableSet<GameTile> = mutableSetOf(),
    val edges: MutableSet<Edge> = mutableSetOf(),
)

/**
 * Models a structure placed on an edge or a node.
 */
data class StructureInfo(
    var structure: CraftanStructure? = null,
)

/**
 * The direction on a tile a node can be in.
 * Contains the direction of the tiles the node also borders if they exist.
 */
enum class NodeDirection(
    val otherTileDirections: Pair<TileDirection, TileDirection>,
) {
    NORTH_WEST(Pair(TileDirection.WEST, TileDirection.NORTH_WEST)),
    NORTH(Pair(TileDirection.NORTH_WEST, TileDirection.NORTH_EAST)),
    NORTH_EAST(Pair(TileDirection.EAST, TileDirection.NORTH_EAST)),
    SOUTH_EAST(Pair(TileDirection.EAST, TileDirection.SOUTH_EAST)),
    SOUTH(Pair(TileDirection.SOUTH_WEST, TileDirection.SOUTH_EAST)),
    SOUTH_WEST(Pair(TileDirection.WEST, TileDirection.SOUTH_WEST)),
}

/**
 * Generates empty nodes for all tiles
 */
fun generateNodes(tiles: List<GameTile>) {
    for (tile in tiles) {
        for (nodeDirection in NodeDirection.entries) {
            if (!tile.nodes.keys.contains(nodeDirection)) {
                val node = Node(StructureInfo())
                node.tiles.add(tile)
                tile.nodes[nodeDirection] = node
                appendNodeToNeigbours(tile, node, tiles, nodeDirection)
            }
        }
    }
}

private fun appendNodeToNeigbours(
    tile: GameTile,
    node: Node,
    tiles: List<GameTile>,
    nodeDirection: NodeDirection,
) {
    val neighbours = getNeighbours(tile, tiles, nodeDirection)
    for (neighbour in neighbours) {
        val neighbourDirection = TileDirection.entries.find { it.tileCoordinate == neighbour.coordinate - tile.coordinate }
        val neighbourNodeDirection = neighborsNodeDirection[nodeDirection]!![neighbourDirection]!!
        neighbour.nodes[neighbourNodeDirection] = node
    }
}

/**
 * For a node in the north direction of a given tile this tells what direction the node has for the bordering tiles.
 * So for the Tile in the north-west direction it would be in the south-east
 */
private val neighborsNodeDirection =
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

private fun getNeighbours(
    tile: GameTile,
    tiles: List<GameTile>,
    nodeDirection: NodeDirection,
): Set<GameTile> {
    val neighbours = mutableSetOf<GameTile>()
    for (tileDirection in nodeDirection.otherTileDirections.toList()) {
        val neighbour = tiles.find { it.coordinate == tile.coordinate + tileDirection.tileCoordinate }
        if (neighbour != null) {
            neighbours.add(neighbour)
        }
    }
    return neighbours
}
