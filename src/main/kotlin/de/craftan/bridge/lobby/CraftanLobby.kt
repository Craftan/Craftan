package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.bridge.events.lobby.LobbyStatusChangedEvent
import de.craftan.bridge.events.lobby.PlayerJoinedLobbyEvent
import de.craftan.bridge.events.lobby.PlayerLeftLobbyEvent
import de.craftan.bridge.map.CraftanMap
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.implementations.CraftanPlayerImpl
import de.craftan.engine.map.maps.DefaultMapLayout
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.commands.to
import de.craftan.io.globalEventBus
import de.craftan.util.toWorldEditWorld
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.server
import net.axay.kspigot.runnables.async
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

/**
 * Models a lobby which holds the players and the ongoing game.
 * @param board the board the game is participating on
 */
class CraftanLobby(
    val id: Int,
    val world: World,
    val gameConfig: CraftanGameConfig,
    val maxPlayers: Int = 4,
    val minPlayers: Int = 3,
    status: CraftanLobbyStatus = CraftanLobbyStatus.WAITING
) {
    private val center = BlockVector3.at(100_000.0, 100.0, 0.0)

    //TODO add layout spacing config option, add map layout through config
    val board = CraftanBoard(world.toWorldEditWorld(), center, 3, DefaultMapLayout())
    val map = CraftanMap(this)

    private var countingDown = false

    private var status = status
        set(value) {
            globalEventBus.fire(LobbyStatusChangedEvent(this, this.status))
            field = value
        }

    private val players = mutableListOf<CraftanPlayer>()
    private val sidebar = Craftan.scoreboardLibrary.createSidebar()

    private var countdown = 0

    init {
        async {
            buildSidebar()
        }
    }

    fun addPlayer(player: Player) {
        val event = PlayerJoinedLobbyEvent(this, player)
        globalEventBus.fire(event)

        if (event.isCancelled) {
            return
        }

        notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))

        teleportToLobby(player)

        players += CraftanPlayerImpl(player)
        sidebar.addPlayer(player)

        sidebar.refreshLines()
    }

    fun removePlayer(player: Player) {
        val craftanPlayer = CraftanPlayerImpl(player)
        players -= craftanPlayer
        notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))
        sidebar.removePlayer(player)

        globalEventBus.fire(PlayerLeftLobbyEvent(this, player))
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun close() {
        sidebar.close()

        notifyPlayers(CraftanNotification.LOBBY_CLOSED)

        //TODO add cleanup on boot as well
        if (!server.isStopping) {
            sync {
                server.unloadWorld(world, false)
                val deleted = world.worldFolder.delete()
                if (!deleted) {
                    Craftan.logger.warning("Failed to delete world ${world.name}")
                }
            }
        }
        CraftanLobbyManager.removeLobby(id)
    }

    fun players() = players.toList()

    fun notifyPlayers(
        notification: CraftanNotification,
        placeholders: Map<CraftanPlaceholder, Component>,
    ) {
        players.forEach { it.sendNotification(notification, placeholders) }
    }

    fun startCountdown() {
        if (countingDown) { return }
        status = CraftanLobbyStatus.STARTING

        countingDown = true

        var currentCountdown = 60

        notifyPlayers(CraftanNotification.LOBBY_STARTED)

        task(
            false,
            0,
            20L,
            currentCountdown.toLong()
        ) {
            currentCountdown--
            countdown = currentCountdown
            buildSidebar()
            players.forEach { it.bukkitPlayer.level = currentCountdown }

            if (currentCountdown <= 5) {
                notifyPlayers(CraftanNotification.LOBBY_STARTS_IN, mapOf(
                    CraftanPlaceholder.TIME_TO_START to literalText(currentCountdown.toString())
                ))
            }

            if (currentCountdown <= 0) {
                players.forEach { it.bukkitPlayer.level = 0 }
                players.forEach { teleportToMap(it.bukkitPlayer) }
                countingDown = false
            }
        }
    }

    private fun teleportToMap(player: Player) = player.teleport(Location(world, center.x() + 0.5, center.y() + 50.0, center.z() + 0.5))

    private fun teleportToLobby(player: Player) = player.teleport(Location(world, 0.0, 100.0, 0.0))

    private fun buildSidebar() {
        val lines = SidebarComponent.builder()
            .addBlankLine()
            .addStaticLine { literalText("Players: ${players().size}/${maxPlayers}") }
            .addDynamicLine { literalText("Map: ${board.layout.name}") }
            .addDynamicLine { literalText("Status: $status") }
            .addBlankLine()
            .addDynamicLine { if (countingDown) literalText("Starting in: $countdown") else literalText() }
            .build()

        val title = SidebarComponent.staticLine(literalText("Craftan"))
        ComponentSidebarLayout(title, lines).apply(sidebar)
    }
}
