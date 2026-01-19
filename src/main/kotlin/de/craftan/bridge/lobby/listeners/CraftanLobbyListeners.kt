package de.craftan.bridge.lobby.listeners

import de.craftan.bridge.events.lobby.*
import de.craftan.bridge.items.LobbyItems
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.bridge.lobby.CraftanPlayerStateManager
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.globalEventBus
import de.staticred.kia.inventory.extensions.setHotbarItem
import net.axay.kspigot.chat.literalText
import org.bukkit.GameMode
import org.bukkit.entity.Player

/**
 * Collection of listeners that handle side-effects for lobby events.
 */
object CraftanLobbyListeners {
    fun register() {
        globalEventBus.on<PlayerJoinedLobbyEvent> {
            val lobby = this.lobby
            val player = this.player

            lobby.notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to literalText(player.name)))

            if (!lobby.sidebar.closed()) {
                lobby.sidebar.addPlayer(player)
            }

            CraftanPlayerStateManager.saveState(player)
            teleportPlayerToLobby(lobby, player)

            lobby.buildAndApplySidebar(player)
            lobby.sidebar.refreshLines()
        }

        globalEventBus.on<PlayerRejoinedLobbyEvent> {
            lobby.notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to literalText(player.name)))

            if (!lobby.sidebar.closed()) {
                lobby.sidebar.addPlayer(player)
            }

            CraftanPlayerStateManager.saveState(player)
            teleportPlayerToMap(lobby, player)

            lobby.buildAndApplySidebar(player)
            lobby.sidebar.refreshLines()
        }

        globalEventBus.on<PlayerLeftLobbyEvent> {
            val lobby = this.lobby
            val player = this.player

            if (!lobby.sidebar.closed()) {
                lobby.sidebar.removePlayer(player)
            }

            CraftanPlayerStateManager.applyState(player)

            lobby.notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to literalText("player.name")))
        }

        globalEventBus.on<PlayerSoftLeftLobbyEvent> {
            if (!lobby.sidebar.closed()) {
                lobby.sidebar.removePlayer(player)
            }

            CraftanPlayerStateManager.applyState(player)
            lobby.notifyPlayers(CraftanNotification.SOFT_LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to literalText(player.name)))
        }

        globalEventBus.on<LobbyCountdownEvent> {
            val lobby = this.lobby
            val seconds = this.secondsLeft
            lobby.players().forEach {
                it.bukkitPlayer.level = seconds
                lobby.buildAndApplySidebar(it.bukkitPlayer)
            }

            if (seconds in 1..5) {
                lobby.notifyPlayers(
                    CraftanNotification.LOBBY_STARTS_IN,
                    mapOf(CraftanPlaceholder.TIME_TO_START to literalText(seconds.toString()))
                )
            }
        }

        globalEventBus.on<PlayerChangedColorEvent> {
            lobby.buildAndApplySidebar(player)
        }

        globalEventBus.on<LobbyStatusChangedEvent> {
            lobby.players().forEach { lobby.buildAndApplySidebar(it.bukkitPlayer) }
        }

        globalEventBus.on<LobbyStartedEvent> {
            lobby.players().forEach { teleportPlayerToMap(lobby, it.bukkitPlayer) }
        }
    }

    private fun teleportPlayerToLobby(lobby: CraftanLobby, player: Player) {
        lobby.teleportToLobby(player)
        player.gameMode = GameMode.ADVENTURE
        player.health = 20.0
        player.foodLevel = 20
        player.inventory.clear()

        player.setHotbarItem(0, LobbyItems.colorSelector(player))
        player.setHotbarItem(8, LobbyItems.startGameItem(player))
    }

    private fun teleportPlayerToMap(lobby: CraftanLobby, player: Player) {
        player.inventory.clear()
        player.gameMode = GameMode.CREATIVE
        player.isFlying = true
        lobby.teleportToMap(player)
        lobby.buildAndApplySidebar(player)
    }
}
