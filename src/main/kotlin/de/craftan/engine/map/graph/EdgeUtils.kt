package de.craftan.engine.map.graph

import de.craftan.engine.map.TileCoordinate

/**
 * Generates the edges for given gametiles
 * The gametiles MUST already have nodes
 * @param tiles to generate edges for: MUST HAVE NODES
 */
fun generateEdges(
    tiles: Set<TileCoordinate>,
    nodes: MutableMap<TileCoordinate, MutableMap<NodeDirection, Node>>,
): MutableMap<TileCoordinate, MutableMap<EdgeDirection, Edge>> {
    val cordToEdgeMap = mutableMapOf<TileCoordinate, MutableMap<EdgeDirection, Edge>>()
    tiles.forEach { cordToEdgeMap[it] = mutableMapOf() }

    for (tile in tiles) {
        var previousNodeDirection = NodeDirection.SOUTH_WEST
        var previousNode = nodes[tile]!![previousNodeDirection]!!
        for (nodeDirection in NodeDirection.entries) {
            val node = nodes[tile]!![nodeDirection]!!

            appendEdge(tiles, tile, previousNode, node, previousNodeDirection, nodeDirection, cordToEdgeMap)

            previousNode = node
            previousNodeDirection = nodeDirection
        }
    }

    return cordToEdgeMap
}

private fun appendEdge(
    tiles: Set<TileCoordinate>,
    tile: TileCoordinate,
    previousNode: Node,
    node: Node,
    previousNodeDirection: NodeDirection,
    nodeDirection: NodeDirection,
    cordToEdgeMap: MutableMap<TileCoordinate, MutableMap<EdgeDirection, Edge>>,
) {
    val edgeNodes = Pair(previousNode, node)
    val edgeDirection = edgeDirectionFromNodeDirections[Pair(previousNodeDirection, nodeDirection)]!!
    val neighbor = tiles.find { it == tile + edgeDirection.otherNode.tileCoordinate }
    val edge: Edge

    neighbor?.let {
        edge =
            Edge(
                setOf(tile, neighbor),
                edgeNodes,
                StructureInfo(),
            )
        cordToEdgeMap[neighbor]!![EdgeDirection.entries.find { it.otherNode.tileCoordinate == edgeDirection.otherNode.tileCoordinate * (-1) }!!] = edge
        cordToEdgeMap[tile]!![edgeDirection] = edge
        return
    }
    edge =
        Edge(
            setOf(tile),
            edgeNodes,
            StructureInfo(),
        )
    cordToEdgeMap[tile]!![edgeDirection] = edge
}

val edgeDirectionFromNodeDirections =
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
