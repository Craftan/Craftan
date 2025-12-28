package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.bridge.events.lobby.*
import de.craftan.bridge.inventory.config.colorSelectorInventory
import de.craftan.bridge.map.CraftanMap
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.implementations.CraftanPlayerImpl
import de.craftan.engine.map.maps.DefaultMapLayout
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.globalEventBus
import de.craftan.io.unloadAndDelete
import de.craftan.util.toWorldEditWorld
import de.staticred.kia.inventory.KInventory
import net.axay.kspigot.chat.col
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.GameMode
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
    val map = CraftanMap(this)

    private var countingDown = false

    var status = status
        private set(value) {
            globalEventBus.fire(LobbyStatusChangedEvent(this, this.status))
            field = value
        }

    private val players = mutableListOf<CraftanPlayer>()
    private val playerColors = mutableMapOf<Player, Color>()

    private var colorSelectorOwner: Player? = null
    private var sharedColorSelectorInventory: KInventory? = null

    val sidebar = Craftan.scoreboardLibrary.createSidebar()

    private var countdown = 0

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
        players.add(CraftanPlayerImpl(player))

        if (colorSelectorOwner == null) {
            colorSelectorOwner = player
        }

        assignMissingColors()

        globalEventBus.fire(PlayerJoinedLobbyEvent(this, player))
    }

    fun removePlayer(player: Player) {
        val craftanPlayer = players.find { it.bukkitPlayer == player } ?: return

        playerColors.remove(player)

        players.remove(craftanPlayer)

        if (colorSelectorOwner == player) {
            colorSelectorOwner = players.firstOrNull()?.bukkitPlayer
            sharedColorSelectorInventory = colorSelectorOwner?.let { colorSelectorInventory(it, this) }
        }
        if (players.isEmpty()) {
            sharedColorSelectorInventory = null
            colorSelectorOwner = null
        }

        globalEventBus.fire(PlayerLeftLobbyEvent(this, player))
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun close() {
        sidebar.close()

        notifyPlayers(CraftanNotification.LOBBY_CLOSED)

        players.toList().forEach { removePlayer(it.bukkitPlayer) }

        world.unloadAndDelete()
        CraftanLobbyManager.removeLobby(id)
    }

    fun players() = players.toList()

    fun allowedColors(): List<Color> = Craftan.configs.gameSettings().colors.map { Color(it.value.color) }

    fun getPlayerColor(player: Player): Color? = playerColors[player]

    fun hasPlayerColor(color: Color): Boolean = playerColors.values.contains(color)

    fun isColorAvailable(color: Color): Boolean = !playerColors.values.contains(color)

    fun setPlayerColor(player: Player, color: Color): Boolean {
        if (!allowedColors().contains(color)) return false
        if (!isColorAvailable(color)) return false
        val from = playerColors[player] ?: return false
        playerColors[player] = color

        globalEventBus.fire(PlayerChangedColorEvent(player, this, from, color))
        return true
    }

    fun assignMissingColors() {
        val available = allowedColors().toMutableList()

        playerColors.values.forEach { used -> available.remove(used) }
        players.forEach { cp ->
            if (playerColors[cp.bukkitPlayer] == null && available.isNotEmpty()) {
                playerColors[cp.bukkitPlayer] = available.removeAt(0)
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
        val lines = SidebarComponent.builder()
            .addBlankLine()
            .addStaticLine { literalText("Players: ${players().size}/${maxPlayers}") }
            .addDynamicLine { literalText("Map: ${board.layout.name}") }
            .addDynamicLine { literalText("Status: $status") }
            .addBlankLine()
            .addDynamicLine { if (countingDown) literalText("Starting in: $countdown") else literalText() }
            .addStaticLine { literalText("Color: ${playerColors[player]?.toString()}") }
            .build()

        val title = SidebarComponent.staticLine(literalText("Craftan"))
        ComponentSidebarLayout(title, lines).apply(sidebar)
    }
}
