package de.craftan.engine.map

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.map.graph.Direction
import de.craftan.engine.map.graph.Edge
import de.craftan.engine.map.graph.Node
import de.craftan.engine.map.graph.NodeDirection
import de.craftan.engine.structures.CraftanStructure
import kotlin.reflect.KClass

interface CraftanMap {
    /**
     * Abstract name of this map layout, for example: default no water
     */
    val name: String

    /**
     * All the Tiles at their coordinates
     */
    val coordinatesToTile: MutableMap<TileCoordinate, GameTile>

    /**
     * Places a CraftanStructure
     */
    fun placeStructure(
        craftanStructure: CraftanStructure,
        coordinate: TileCoordinate,
        direction: Direction,
        owner:CraftanPlayer
    ) {
        val tile = coordinatesToTile[coordinate]!!

        if (direction is NodeDirection) {
            val node = tile.nodes[direction]!!
            node.structureInfo.structure = craftanStructure
            node.structureInfo.owner = owner
        } else {
            val edge = tile.edges[direction]!!
            edge.structureInfo.structure = craftanStructure
            edge.structureInfo.owner = owner
        }
    }

    fun <T : CraftanStructure> distanceBetweenStructuresAtleast(
        tile: TileCoordinate,
        direction: Direction,
        minDistance: Int,
        structureType: KClass<T>,
    ): Boolean {
        if (coordinatesToTile[tile] == null) throw IllegalArgumentException("The Tile given does not exist")

        if (direction is NodeDirection) {
            var currentDepth = 0
            val alreadyVisited = mutableSetOf(coordinatesToTile[tile]!!.nodes[direction]!!)
            var neighbors = mutableSetOf<Node>()
            coordinatesToTile[tile]!!.nodes[direction]!!.edges.forEach {
                if (!alreadyVisited.contains(it.nodes.first)) neighbors.add(it.nodes.first)
                if (!alreadyVisited.contains(it.nodes.second)) neighbors.add(it.nodes.second)
            }
            var newNeighbors = mutableSetOf<Node>()
            while (currentDepth < minDistance) {
                for (neighbor in neighbors) {
                    if ((neighbor.structureInfo.structure != null) &&
                        (neighbor.structureInfo.structure!!::class == structureType)
                    ) {
                        return false
                    }
                    neighbor.edges.forEach {
                        if (!alreadyVisited.contains(it.nodes.first)) neighbors.add(it.nodes.first)
                        if (!alreadyVisited.contains(it.nodes.second)) neighbors.add(it.nodes.second)
                    }
                }
                neighbors = newNeighbors
                newNeighbors = mutableSetOf()
                currentDepth++
            }
        } else {
            var currentDepth = 0
            val alreadyVisited = mutableSetOf(coordinatesToTile[tile]!!.edges[direction]!!)
            var neighbors = mutableSetOf<Edge>()
            coordinatesToTile[tile]!!.edges[direction]!!.nodes.toList().forEach { node ->
                node.edges.forEach { edge -> if (!alreadyVisited.contains(edge)) neighbors.add(edge) }
            }
            var newNeighbors = mutableSetOf<Edge>()
            while (currentDepth < minDistance) {
                for (neighbor in neighbors) {
                    if ((neighbor.structureInfo.structure != null) &&
                        (neighbor.structureInfo.structure!!::class == structureType)
                    ) {
                        return false
                    }
                    neighbor.nodes.toList().forEach { node ->
                        node.edges.forEach { edge -> if (!alreadyVisited.contains(edge)) neighbors.add(edge) }
                    }
                }
                neighbors = newNeighbors
                newNeighbors = mutableSetOf()
                currentDepth++
            }
        }

        return true
    }
}
