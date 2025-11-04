package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.PluginManager
import de.craftan.bridge.world.generateEmptyWorld
import de.craftan.config.ConfigSystem
import de.craftan.config.CraftanConfig
import de.craftan.engine.map.CraftanMapLayout
import de.craftan.util.toWorldEditWorld

object LobbyManager {

    private val lobbies = mutableMapOf<Int, CraftanLobby>()

    /**
     * Generates a new map and builds the lobby
     * @return the finished lobby
     */
    fun createLobby(layout: CraftanMapLayout): CraftanLobby {
        val world = generateEmptyWorld("craftan-lobby-${lobbies.size}")
        val config = ConfigSystem.config

        val board = CraftanBoard(world.toWorldEditWorld(), BlockVector3.at(0, 100, 0), 3, layout)

        return CraftanLobby(board, MutableCraftanSettings(mutableListOf(), config[CraftanConfig.defaultPointsToWin], config[CraftanConfig.defaultDiscardLimit]))
    }

    fun listLobbies() = lobbies.toMap()


}