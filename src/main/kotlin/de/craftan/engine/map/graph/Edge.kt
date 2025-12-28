package de.craftan.engine.map.graph

import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.TileDirection

/**
 * An edge connects the nodes and lies between up to 2 tiles
 */
data class Edge(
    val tiles: Set<TileCoordinate>,
    val nodes: Pair<Node, Node>,
    var structureInfo: StructureInfo? = null,
)