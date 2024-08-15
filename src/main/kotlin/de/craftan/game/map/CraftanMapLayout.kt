package de.craftan.game.map

import de.craftan.game.lobby.CraftanLobby
import kotlin.math.max
import kotlin.math.min

interface CraftanMapLayout {
    val tiles: List<LayoutRow<GameTile>>
    val includesWater: Boolean

    /**
     * Builds the map from [tiles]
     *
     * The maps layout is always as follows:
     *
     * ^x (minecraft)
     * |
     * |----> y (minecraft)
     *
     * so in the x coordinate is actually up in the map,
     * and y is left and right.
     *
     * @param lobby where to build the map
     */
    fun build(lobby: CraftanLobby) {
        val center = lobby.center

        if (tiles.size % 2 == 0) {
            error("Maps with odd numbers of tiles are not supported yet!")
        }

        val hexSize = 35
        val hexToSide = 18
        val spacing = 3

        val centerRowIndex = tiles.size / 2 + 1

        for ((rowIndex, row) in tiles.withIndex()) {
            val tiles = row.tiles

            val deltaToCenter = max(rowIndex, centerRowIndex) - min(rowIndex, centerRowIndex)
            val isAboveCenter = rowIndex < centerRowIndex

            val xOffset = (hexSize + spacing) * deltaToCenter
        }
    }
}
