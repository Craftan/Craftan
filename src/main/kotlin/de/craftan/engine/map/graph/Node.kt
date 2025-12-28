package de.craftan.engine.map.graph

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.TileDirection
import de.craftan.engine.structures.CraftanStructure

/**
 * A Node is practically the place where the players can build settlements and cities.
 * It is the intersection of up to 3 tiles and is connected to other nodes via the edges connected to it
 */
data class Node(
    var structureInfo: StructureInfo? = null,
    val tiles: MutableSet<TileCoordinate> = mutableSetOf(),
    val edges: MutableSet<Edge> = mutableSetOf(),
)

/**
 * Models a structure placed on an edge or a node.
 */
data class StructureInfo(
    var structure: CraftanStructure,
    val owner: CraftanPlayer,
)
