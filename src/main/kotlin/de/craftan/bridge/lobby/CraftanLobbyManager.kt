package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.bridge.events.lobby.LobbyCreatedEvent
import de.craftan.bridge.util.sendNotification
import de.craftan.bridge.world.generateEmptyWorld
import de.craftan.engine.CraftanGameConfig
import de.craftan.io.CraftanEventBus
import de.craftan.io.CraftanNotification
import de.craftan.io.globalEventBus
import de.craftan.structures.loadStructureToClipboard
import de.craftan.structures.placeStructure
import de.craftan.util.toWorldEditWorld
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File

object CraftanLobbyManager {

    private val lobbies = mutableMapOf<Int, CraftanLobby>()
    private val eventBus: CraftanEventBus = globalEventBus

    /**
     * Generates a new map and builds the lobby
     * @return the finished lobby
     */
    @Synchronized
    fun createLobby(config: CraftanGameConfig): CraftanLobby {
        val id = lobbies.size + 1

        //TODO add logger for world building
        val world = generateEmptyWorld("lobby-$id")
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)

        world.time = 12000
        world.weatherDuration = 0

        placeLobbyInWorld(world = world)

        val lobby = CraftanLobby(id, world, config)
        lobbies[lobbies.size] = lobby

        eventBus.fire(LobbyCreatedEvent(lobby))
        return lobby
    }

    /**
     * Generates a new map and builds the lobby and notifies the given player
     */
    @Synchronized
    fun createLobbyWithPlayer(config: CraftanGameConfig, player: Player): CraftanLobby {
        player.sendNotification(CraftanNotification.LOBBY_WORLD_BUILDING)
        val lobby = createLobby(config)
        lobby.addPlayer(player)
        return lobby
    }

    fun placeLobbyInWorld(world: World) {
        val waitingLobby = loadStructureToClipboard(File(Craftan.schematicsFolder, "lobby.schem"))
        placeStructure(BlockVector3.at(0, 100, 0), world.toWorldEditWorld(), waitingLobby!!)
    }

    fun listLobbies() = lobbies.toMap()

    fun isInLobby(player: Player): Boolean {
        return lobbies.values.any { it.players().map { player -> player.bukkitPlayer }.contains(player) }
    }

    fun getLobbyForPlayer(player: Player): CraftanLobby? {
        return lobbies.values.firstOrNull { it.players().map { player -> player.bukkitPlayer }.contains(player) }
    }

    fun removeLobby(id: Int) {
        lobbies.remove(id)
    }

    fun removePlayerFromLobby(player: Player) {
        getLobbyForPlayer(player)?.removePlayer(player)
    }

    fun getLobbyById(id: Int) = lobbies[id]

    fun closeAllLobbies() {
        lobbies.values.forEach { it.close() }
    }
}