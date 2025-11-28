package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.bridge.events.lobby.LobbyCountdownEvent
import de.craftan.bridge.events.lobby.LobbyStartedEvent
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
import org.apache.commons.io.FileUtils
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

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
    private val center = BlockVector3.at(10_000.0, 100.0, 0.0)

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

        players += CraftanPlayerImpl(player, origin = player.location)
        sidebar.addPlayer(player)

        teleportToLobby(player)
        player.gameMode = GameMode.ADVENTURE
        player.inventory.clear()

        sidebar.refreshLines()
    }

    fun removePlayer(player: Player) {
        val craftanPlayer = players.find { it.bukkitPlayer == player } ?: return
        players.remove(craftanPlayer)

        if (!sidebar.closed()) {
            sidebar.removePlayer(player)
        }


        player.teleport(craftanPlayer.origin)

        notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))
        globalEventBus.fire(PlayerLeftLobbyEvent(this, player))
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun close() {
        sidebar.close()

        notifyPlayers(CraftanNotification.LOBBY_CLOSED)

        players.toList().forEach { removePlayer(it.bukkitPlayer) }

        //TODO add cleanup on boot as well
        if (!server.isStopping) {
            task(
                true,
                40L,
                ) {

                Craftan.logger.info("Unloading world ${world.name}")

                val result = server.unloadWorld(world, false)

                if (!result) {
                    Craftan.logger.warning("Failed to save world ${world.name}")
                    return@task
                }

                Craftan.logger.info("Deleting world ${world.name}")
                runCatching { FileUtils.deleteDirectory(world.worldFolder) }.onFailure {
                    Craftan.logger.info("Failed deleting world ${world.name}")
                    it.printStackTrace()
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

        var currentCountdown = 20

        notifyPlayers(CraftanNotification.LOBBY_STARTED)

        task(
            true,
            0,
            20L,
        ) {
            currentCountdown--
            countdown = currentCountdown
            buildSidebar()
            players.forEach { it.bukkitPlayer.level = currentCountdown }

            globalEventBus.fire(LobbyCountdownEvent(this, currentCountdown))

            if (currentCountdown <= 5) {
                notifyPlayers(CraftanNotification.LOBBY_STARTS_IN, mapOf(
                    CraftanPlaceholder.TIME_TO_START to literalText(currentCountdown.toString())
                ))
            }

            if (currentCountdown <= 0) {
                it.cancel()
                countingDown = false
                start()
            }
        }
    }

    private fun start() {
        status = CraftanLobbyStatus.IN_GAME

        players.forEach { player ->  run {
                player.bukkitPlayer.gameMode = GameMode.CREATIVE
                player.bukkitPlayer.isFlying = true
                teleportToMap(player.bukkitPlayer)
            }
        }

        globalEventBus.fire(LobbyStartedEvent(this))
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
