package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.bridge.events.lobby.*
import de.craftan.bridge.inventory.config.colorSelectorInventory
import de.craftan.bridge.items.LobbyItems
import de.craftan.bridge.map.CraftanMap
import de.craftan.engine.CraftanGameAction
import de.craftan.engine.CraftanGameActionEvent
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.implementations.CraftanPlayerImpl
import de.craftan.engine.map.maps.DefaultMapLayout
import de.craftan.io.CraftanEvent
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.commands.to
import de.craftan.io.globalEventBus
import de.craftan.io.unloadAndDelete
import de.craftan.util.toWorldEditWorld
import de.staticred.kia.inventory.KInventory
import de.staticred.kia.inventory.extensions.setHotbarItem
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.server
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import net.ormr.eventbus.EventBus
import org.apache.commons.io.FileUtils
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

    private val sidebar = Craftan.scoreboardLibrary.createSidebar()

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
        val event = PlayerJoinedLobbyEvent(this, player)
        globalEventBus.fire(event)

        if (event.isCancelled) {
            return
        }

        notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))

        players.add(CraftanPlayerImpl(player, origin = player.location))
        CraftanPlayerStateManager.saveState(player)
        sidebar.addPlayer(player)

        if (colorSelectorOwner == null) {
            colorSelectorOwner = player
        }

        teleportToLobby(player)
        player.gameMode = GameMode.ADVENTURE
        player.inventory.clear()

        assignMissingColors()
        buildAndApplySidebar(player)

        player.setHotbarItem(0, LobbyItems.colorSelector(player))

        sidebar.refreshLines()
    }

    fun removePlayer(player: Player) {
        val craftanPlayer = players.find { it.bukkitPlayer == player } ?: return

        playerColors.remove(player)

        if (!sidebar.closed()) {
            sidebar.removePlayer(player)
        }

        player.teleport(craftanPlayer.origin)
        players.remove(craftanPlayer)

        if (colorSelectorOwner == player) {
            colorSelectorOwner = players.firstOrNull()?.bukkitPlayer
            sharedColorSelectorInventory = colorSelectorOwner?.let { colorSelectorInventory(it, this) }
        }
        if (players.isEmpty()) {
            sharedColorSelectorInventory = null
            colorSelectorOwner = null
        }

        notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))
        CraftanPlayerStateManager.applyState(player)

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

    fun allowedColors(): List<Color> = listOf(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE)

    fun getPlayerColor(player: Player): Color? = playerColors[player]

    fun hasPlayerColor(color: Color): Boolean = playerColors.values.contains(color)

    fun isColorAvailable(color: Color): Boolean = !playerColors.values.contains(color)

    fun setPlayerColor(player: Player, color: Color): Boolean {
        if (!allowedColors().contains(color)) return false
        if (!isColorAvailable(color)) return false
        playerColors[player] = color
        buildAndApplySidebar(player)
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
        assignMissingColors()

        status = CraftanLobbyStatus.IN_GAME

        players.forEach { player ->
            player.bukkitPlayer.gameMode = GameMode.CREATIVE
            player.bukkitPlayer.isFlying = true
            teleportToMap(player.bukkitPlayer)
        }

        globalEventBus.fire(LobbyStartedEvent(this))
    }

    private fun teleportToMap(player: Player) = player.teleport(Location(world, center.x() + 0.5, center.y() + 50.0, center.z() + 0.5))

    private fun teleportToLobby(player: Player) = player.teleport(Location(world, 0.0, 100.0, 0.0))

    private fun buildAndApplySidebar(player: Player) {
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
