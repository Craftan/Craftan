package de.craftan.engine.map

interface CraftanMap {
    /**
     * Abstract name of this map layout, for example: default no water
     */
    val name: String

    /**
     * All the Tiles at their coordinates
     */
    val coordinatesToTile: MutableMap<TileCoordinate, GameTile>
}
