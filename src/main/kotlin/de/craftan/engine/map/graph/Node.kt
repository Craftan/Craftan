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
    var structureInfo: StructureInfo,
    val tiles: MutableSet<TileCoordinate> = mutableSetOf(),
    val edges: MutableSet<Edge> = mutableSetOf(),
)

/**
 * Models a structure placed on an edge or a node.
 */
data class StructureInfo(
    var structure: CraftanStructure? = null,
    var owner: CraftanPlayer? = null,
)

/**
 * The direction on a tile a node can be in.
 * Contains the direction of the tiles the node also borders if they exist.
 */
enum class NodeDirection(
    val otherTileDirections: Pair<TileDirection, TileDirection>,
) : Direction {
    NORTH_WEST(Pair(TileDirection.WEST, TileDirection.NORTH_WEST)),
    NORTH(Pair(TileDirection.NORTH_WEST, TileDirection.NORTH_EAST)),
    NORTH_EAST(Pair(TileDirection.EAST, TileDirection.NORTH_EAST)),
    SOUTH_EAST(Pair(TileDirection.EAST, TileDirection.SOUTH_EAST)),
    SOUTH(Pair(TileDirection.SOUTH_WEST, TileDirection.SOUTH_EAST)),
    SOUTH_WEST(Pair(TileDirection.WEST, TileDirection.SOUTH_WEST)),
}

interface Direction
