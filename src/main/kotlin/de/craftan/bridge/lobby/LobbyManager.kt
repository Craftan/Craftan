package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.bridge.world.generateEmptyWorld
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.map.CraftanMapLayout
import de.craftan.util.toWorldEditWorld
import org.bukkit.entity.Player

object LobbyManager {

    private val lobbies = mutableMapOf<Int, CraftanLobby>()

    /**
     * Generates a new map and builds the lobby
     * @return the finished lobby
     */
    fun createLobby(layout: CraftanMapLayout, config: CraftanGameConfig): CraftanLobby {
        val world = generateEmptyWorld("craftan-lobby-${lobbies.size}")

        val board = CraftanBoard(world.toWorldEditWorld(), BlockVector3.at(0, 100, 0), 3, layout)

        val lobby = CraftanLobby(board, config)
        lobbies[lobbies.size] = lobby
        return lobby

    }

    fun listLobbies() = lobbies.toMap()

    fun isInLobby(player: Player): Boolean {
        return lobbies.values.any { it.players().map { player -> player.bukkitPlayer }.contains(player) }
    }

    fun getLobbyForPlayer(player: Player): CraftanLobby? {
        return lobbies.values.firstOrNull { it.players().map { player -> player.bukkitPlayer }.contains(player) }
    }

    fun removePlayerFromLobby(player: Player) {
        getLobbyForPlayer(player)?.removePlayer(player)
    }

}