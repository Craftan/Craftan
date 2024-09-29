package de.craftan.engine.map.graph

import de.craftan.engine.map.GameTile
import de.craftan.engine.map.TileDirection

/**
 * An edge connects the nodes and lies between up to 2 tiles
 */
data class Edge(
    val tiles: Set<GameTile>,
    val nodes: Pair<Node, Node>,
    val structureInfo: StructureInfo,
)

enum class EdgeDirection(
    val nodeDirections: Pair<NodeDirection, NodeDirection>,
    val otherNode: TileDirection,
) {
    NORTH_WEST(Pair(NodeDirection.NORTH, NodeDirection.NORTH_WEST), TileDirection.NORTH_WEST),
    NORTH_EAST(Pair(NodeDirection.NORTH, NodeDirection.NORTH_EAST), TileDirection.NORTH_EAST),
    EAST(Pair(NodeDirection.NORTH_EAST, NodeDirection.SOUTH_EAST), TileDirection.EAST),
    SOUTH_EAST(Pair(NodeDirection.SOUTH, NodeDirection.NORTH_EAST), TileDirection.SOUTH_EAST),
    SOUTH_WEST(Pair(NodeDirection.SOUTH, NodeDirection.NORTH_WEST), TileDirection.SOUTH_WEST),
    WEST(Pair(NodeDirection.NORTH_WEST, NodeDirection.SOUTH_WEST), TileDirection.WEST),
}

/**
 * Generates the edges for given gametiles
 * The gametiles MUST already have nodes
 * @param tiles to generate edges for: MUST HAVE NODES
 */
fun generateEdges(tiles: List<GameTile>) {
    for (tile in tiles) {
        var previousNodeDirection = NodeDirection.NORTH_WEST
        var previousNode = tile.nodes[previousNodeDirection]!!
        for (nodeDirection in NodeDirection.entries) {
            val node = tile.nodes[nodeDirection]!!

            appendEdge(tiles, tile, previousNode, node, previousNodeDirection, nodeDirection)

            previousNode = node
            previousNodeDirection = nodeDirection
        }
    }
}

private fun appendEdge(
    tiles: List<GameTile>,
    tile: GameTile,
    previousNode: Node,
    node: Node,
    previousNodeDirection: NodeDirection,
    nodeDirection: NodeDirection,
) {
    val edgeNodes = Pair(previousNode, node)
    val edgeDirection = edgeDirectionFromNodeDirections[Pair(previousNodeDirection, nodeDirection)]!!
    val neighbor = tiles.find { it.coordinate == tile.coordinate + edgeDirection.otherNode.tileCoordinate }
    val edge: Edge
    if (neighbor != null) {
        edge =
            Edge(
                setOf(tile, neighbor),
                edgeNodes,
                StructureInfo(),
            )
        neighbor.edges[EdgeDirection.entries.find { it.otherNode.tileCoordinate == edgeDirection.otherNode.tileCoordinate * (-1) }!!] = edge
    } else {
        edge =
            Edge(
                setOf(tile),
                edgeNodes,
                StructureInfo(),
            )
    }
    tile.edges[edgeDirection] = edge
}

private val edgeDirectionFromNodeDirections =
    mapOf(
        Pair(
            Pair(NodeDirection.NORTH_WEST, NodeDirection.NORTH),
            EdgeDirection.NORTH_WEST,
        ),
        Pair(
            Pair(NodeDirection.NORTH, NodeDirection.NORTH_EAST),
            EdgeDirection.NORTH_EAST,
        ),
        Pair(
            Pair(NodeDirection.NORTH_EAST, NodeDirection.SOUTH_EAST),
            EdgeDirection.EAST,
        ),
        Pair(
            Pair(NodeDirection.SOUTH_EAST, NodeDirection.SOUTH),
            EdgeDirection.SOUTH_EAST,
        ),
        Pair(
            Pair(NodeDirection.SOUTH, NodeDirection.SOUTH_WEST),
            EdgeDirection.SOUTH_WEST,
        ),
        Pair(
            Pair(NodeDirection.SOUTH_WEST, NodeDirection.NORTH_WEST),
            EdgeDirection.WEST,
        ),
    )
