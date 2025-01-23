package de.craftan.engine.map

import de.craftan.bridge.map.CraftanMapBuilder
import de.craftan.engine.map.graph.*

/**
 * The coordinates of a tile.
 * How these coordinates work? See https://www.redblobgames.com/grids/hexagons/
 *
 * TLDR
 * q, r, and s represent the axis a hexagon can have
 * r represents the horizontal axis
 * q the top left to bottom right axis
 * s the top right to bottom left axis
 *
 *  r = 0, corresponds to the middle of the map. Going north, decreases the r coordinate, south increases it.
 *  q = 0, corresponds to the middle of the map. Going west, decreases the q coordinate, east increases it.
 *  s = 0, corresponds to the middle of the map. Going east, decreases the s coordinate, west increases it.
 *
 *
 * Since we have 3 variables for a 2-dimensional plane they are dependent on each other
 * Thus the constraint of: r + s + q = 0
 */
data class TileCoordinate(
    val q: Int = 0,
    val r: Int = 0,
    val s: Int = 0,
) {
    init {
        if (q + r + s != 0) {
            throw IllegalArgumentException("A coordinate contains a contradiction: Expected q + r + s = 0, got q = $q, got r = $r, got s = $s ")
        }
    }

    companion object {
        /**
         * Converts the row, column coordinates in to q r s coordinates
         * see https://www.redblobgames.com/grids/hexagons/
         */
        fun fromOffsetCoordinates(
            rowCoordinate: Int,
            columnCoordinate: Int,
        ): TileCoordinate {
            val q: Int = columnCoordinate - (rowCoordinate - (rowCoordinate and 1)) / 2
            val r: Int = rowCoordinate
            val s: Int = -q - r
            return TileCoordinate(q, r, s)
        }
    }

    operator fun plus(coordinate: TileCoordinate): TileCoordinate = TileCoordinate(q + coordinate.q, r + coordinate.r, s + coordinate.s)

    operator fun minus(coordinate: TileCoordinate): TileCoordinate = TileCoordinate(q - coordinate.q, r - coordinate.r, s - coordinate.s)

    operator fun times(scalar: Int): TileCoordinate = TileCoordinate(q * scalar, r * scalar, s * scalar)
}

/**
 * The directions to the neighboring tiles, with the corresponding coordinate change.
 *
 * @param tileCoordinate the corresponding coordinate change
 */
enum class TileDirection(
    val tileCoordinate: TileCoordinate,
) {
    NORTH_EAST(TileCoordinate(1, -1, 0)),
    NORTH_WEST(TileCoordinate(0, -1, 1)),
    SOUTH_EAST(TileCoordinate(0, 1, -1)),
    SOUTH_WEST(TileCoordinate(-1, 1, 0)),
    WEST(TileCoordinate(-1, 0, 1)),
    EAST(TileCoordinate(1, 0, -1)),
}

/**
 * Contains the catan relevant attributes a tile can have.
 * Meaning the type of material you can get and the DiceNumber you get it at
 */
data class TileInfo(
    val type: MaterialType,
    val chance: DiceNumber,
)

data class GameTile(
    val coordinate: TileCoordinate,
    val tileInfo: TileInfo,
    val nodes: Map<NodeDirection, Node>,
    val edges: Map<EdgeDirection, Edge>,
)

/**
 * Converts a list of rows of TileInformation to GameTile`s.
 * Earlier in the outer List means the row is more up in the board.
 * Earlier in the inner List means more to the left on the game board
 * @param tilesInfo a list of rows of TileInformation
 * @see CraftanMapBuilder
 */
fun toGameTiles(tilesInfo: List<List<TileInfo>>): List<GameTile> {
    val gametiles = mutableListOf<GameTile>()
    val tileCoordinates: MutableSet<TileCoordinate> = mutableSetOf()
    val cordToTileInfo: MutableMap<TileCoordinate, TileInfo> = mutableMapOf()

    val rowCenter = tilesInfo.size / 2

    for ((rowIndex, row) in tilesInfo.withIndex()) {
        val rowCoordinate = rowIndex - rowCenter
        for ((columnIndex, tileInfo) in row.withIndex()) {
            val columnmidel = row.size / 2
            val columnCoordinate = columnIndex - columnmidel

            val coordinate = TileCoordinate.fromOffsetCoordinates(rowCoordinate, columnCoordinate)
            tileCoordinates.add(coordinate)
            cordToTileInfo[coordinate] = tileInfo
        }
    }

    val cordToNodes = generateNodes(tileCoordinates)
    val cordToEdges = generateEdges(tileCoordinates, cordToNodes)

    for (tile in tileCoordinates) {
        gametiles.add(GameTile(tile, cordToTileInfo[tile]!!, cordToNodes[tile]!!, cordToEdges[tile]!!))
    }

    return gametiles
}

/**
 * Converts a list of rows of TileInformation to a map from the coordinate to the corresponding GameTile.
 * Earlier in the outer List means the row is more up in the board.
 * Earlier in the inner List means more to the left on the game board
 * @param tilesInfo list of rows of TileInformation
 */
fun toCoordinateToGameTileMap(tilesInfo: List<List<TileInfo>>): MutableMap<TileCoordinate, GameTile> {
    val gameTiles = toGameTiles(tilesInfo)
    val map: MutableMap<TileCoordinate, GameTile> = mutableMapOf()
    gameTiles.forEach { map.put(it.coordinate, it) }
    return map
}
