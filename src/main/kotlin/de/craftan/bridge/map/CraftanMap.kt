package de.craftan.bridge.map

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.engine.map.GameTile
import org.bukkit.World

/**
 * Models the actual ingame map of a craftan game
 * Will be produced by the provided Map Layout.
 *
 * It works by having a centered tile, and using the IJK coordinate system to keep track of the tiles.
 * The centered tile is always calculated as the following:
 *
 * =Center tile of the center row.
 *
 * @see CraftanMapLayout
 * @see de.craftan.engine.CraftanGame
 */
class CraftanMap(
    val usedLayout: CraftanMapLayout,
) {
    private val tiles = mutableListOf<MapTile>()

    private val coordinatesToTile = mutableMapOf<TileCoordinate, MapTile>()

    fun initTiles() {
        val rows = usedLayout.rows

        for (row in rows) {
        }
    }

    fun createTile() {
    }

    fun addTile(tile: MapTile) {
        tiles += tile
    }
}

data class TileCoordinate(
    val i: Int,
    val j: Int,
    val k: Int,
)

data class MapTile(
    val coordinate: TileCoordinate,
    val size: HexagonSize,
    val center: BlockVector3,
    val world: World,
    val gameTile: GameTile,
)
