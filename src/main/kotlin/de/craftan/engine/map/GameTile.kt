package de.craftan.engine.map

import de.craftan.bridge.map.CraftanMap

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
)

/**
 * Converts a list of rows of TileInformation to GameTile`s.
 * Earlier in the outer List means the row is more up in the board.
 * Earlier in the inner List means more to the left on the game board
 * @param tilesInfo a list of rows of TileInformation
 * @see CraftanMap
 */
fun toGameTiles(tilesInfo: List<List<TileInfo>>): List<GameTile> {
    val gametiles = mutableListOf<GameTile>()

    val rowCenter = tilesInfo.size / 2

    for ((rowIndex, row) in tilesInfo.withIndex()) {
        val rowCoordinate = rowIndex - rowCenter
        for ((columnIndex, tileInfo) in row.withIndex()) {
            val columnmidel = row.size / 2
            val columnCoordinate = columnIndex - columnmidel

            val coordinate = TileCoordinate.fromOffsetCoordinates(rowCoordinate, columnCoordinate)
            gametiles.add(GameTile(coordinate, tileInfo))
        }
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
