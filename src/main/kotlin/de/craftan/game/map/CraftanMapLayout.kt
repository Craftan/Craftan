package de.craftan.game.map

import de.craftan.game.lobby.CraftanLobby

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
     * |----> z (minecraft)
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
        val spacing = lobby.spacing
    }
}
