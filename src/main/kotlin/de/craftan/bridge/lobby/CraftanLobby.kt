package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.bridge.CraftanBridgePlayerImpl
import de.craftan.bridge.CraftanTeam
import de.craftan.bridge.events.lobby.*
import de.craftan.bridge.inventory.config.colorSelectorInventory
import de.craftan.bridge.lobby.scoreboard.ConfigurableScoreboard
import de.craftan.bridge.lobby.scoreboard.ScoreboardProvider
import de.craftan.bridge.map.CraftanBridgeMap
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.map.maps.DefaultMapLayout
import de.craftan.io.*
import de.craftan.util.toWorldEditWorld
import de.staticred.kia.inventory.KInventory
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.awt.Color

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
    val map = CraftanBridgeMap(this)

    var countingDown = false
        private set

    var status = status
        private set(value) {
            globalEventBus.fire(LobbyStatusChangedEvent(this, this.status))
            field = value
        }

    private val players = mutableListOf<CraftanBridgePlayerImpl>()

    private val scoreboardProvider: ScoreboardProvider = ConfigurableScoreboard()

    private var colorSelectorOwner: Player? = null
    private var sharedColorSelectorInventory: KInventory? = null

    val sidebar = Craftan.scoreboardLibrary.createSidebar()

    var countdown = 0
        private set

    fun getSharedColorSelectorInventory(requestingPlayer: Player): KInventory {
        if (sharedColorSelectorInventory == null) {
            if (colorSelectorOwner == null) {
                colorSelectorOwner = requestingPlayer
            }
            sharedColorSelectorInventory = colorSelectorInventory(colorSelectorOwner!!, this)
        }
        return sharedColorSelectorInventory!!
    }

    fun addPlayer(player: Player) {
        players.add(CraftanBridgePlayerImpl(player, team = null))

        if (colorSelectorOwner == null) {
            colorSelectorOwner = player
        }

        assignMissingColors()

        globalEventBus.fire(PlayerJoinedLobbyEvent(this, player))
    }

    fun removePlayer(player: Player) {
        val craftanPlayer = players.find { it.bukkitPlayer == player } ?: return

        craftanPlayer.team = null

        players.remove(craftanPlayer)

        if (colorSelectorOwner == player) {
            colorSelectorOwner = players.firstOrNull()?.bukkitPlayer
            sharedColorSelectorInventory = colorSelectorOwner?.let { colorSelectorInventory(it, this) }
        }

        globalEventBus.fire(PlayerLeftLobbyEvent(this, player))

        if (players.isEmpty()) {
            close()
        }
    }

    fun softRemovePlayer(player: Player) {
        Craftan.logger.debug("Soft removing player $player from lobby $id")
        players.firstOrNull { it.bukkitPlayer == player }?.let { it.left = true }
        globalEventBus.fire(PlayerSoftLeftLobbyEvent(this, player))
    }

    fun rejoinGame(player: Player) {
        Craftan.logger.debug("Rejoining player $player from lobby $id")
        players.firstOrNull { it.bukkitPlayer.uniqueId == player.uniqueId }?.let {
            it.left = false
            it.bukkitPlayer = player
        }

        globalEventBus.fire(PlayerRejoinedLobbyEvent(this, player))
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun close() {
        Craftan.logger.debug("Closing lobby $id")
        sidebar.close()

        notifyPlayers(CraftanNotification.LOBBY_CLOSED)

        players.toList().forEach { removePlayer(it.bukkitPlayer) }

        world.unloadAndDelete()
        CraftanLobbyManager.removeLobby(id)
        globalEventBus.fire(LobbyStoppedEvent(this))
    }

    fun players() = players.toList()

    fun allowedTeams(): List<CraftanTeam> = Craftan.configs.gameSettings().colors.map { CraftanTeam(Color(it.value.color), it.value.displayName) }

    fun getPlayerColor(player: Player): Color? = craftanPlayer(player)?.team?.color

    fun craftanPlayer(player: Player) = players.find { it.bukkitPlayer == player }

    fun hasPlayerColor(color: Color): Boolean = players.any { it.team?.color == color }

    fun isColorAvailable(color: Color): Boolean = !hasPlayerColor(color)

    fun setPlayerColor(player: Player, color: Color, name: String): Boolean {
        if (!allowedTeams().map { it.color }.contains(color)) return false
        if (!isColorAvailable(color)) return false
        val from = getPlayerColor(player) ?: return false

        val team = CraftanTeam(color, name)
        craftanPlayer(player)?.team = team

        globalEventBus.fire(PlayerChangedColorEvent(player, this, from, color))
        return true
    }

    fun assignMissingColors() {
        val available = allowedTeams().toMutableList()

        players.mapNotNull { it.team }.forEach { available.remove(it) }

        players.filter { it.team == null }.forEach { player ->
            if (available.isNotEmpty()) {
                 player.team = available.removeAt(0)
            }
        }
    }

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
            players.forEach {
                it.bukkitPlayer.level = currentCountdown
                buildAndApplySidebar(it.bukkitPlayer)
            }

            globalEventBus.fire(LobbyCountdownEvent(this, currentCountdown))

            if (currentCountdown <= 0) {
                it.cancel()
                countingDown = false
                start()
            }
        }
    }

    private fun start() {
        assignMissingColors()

        status = CraftanLobbyStatus.IN_GAME

        globalEventBus.fire(LobbyStartedEvent(this))
    }

    fun teleportToMap(player: Player) = player.teleport(Location(world, center.x() + 0.5, center.y() + 50.0, center.z() + 0.5))

    fun teleportToLobby(player: Player) = player.teleport(Location(world, 0.0, 100.0, 0.0))

    fun buildAndApplySidebar(player: Player) {
        val lines = scoreboardProvider.build(player, this)
        val title = scoreboardProvider.buildTitle(player, this)

        ComponentSidebarLayout(SidebarComponent.staticLine(title), lines).apply(sidebar)
    }
}
