package de.craftan.bridge.lobby.listeners

import de.craftan.bridge.events.lobby.LobbyCountdownEvent
import de.craftan.bridge.events.lobby.LobbyStartedEvent
import de.craftan.bridge.events.lobby.PlayerChangedColorEvent
import de.craftan.bridge.events.lobby.PlayerJoinedLobbyEvent
import de.craftan.bridge.events.lobby.PlayerLeftLobbyEvent
import de.craftan.bridge.items.LobbyItems
import de.craftan.bridge.lobby.CraftanPlayerStateManager
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.globalEventBus
import de.staticred.kia.inventory.extensions.setHotbarItem
import net.axay.kspigot.chat.literalText
import org.bukkit.GameMode

/**
 * Collection of listeners that handle side-effects for lobby events.
 */
object LobbyEventListeners {
    fun register() {
        globalEventBus.on<PlayerJoinedLobbyEvent> {
            val lobby = this.lobby
            val player = this.player

            lobby.notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to literalText(player.name)))

            CraftanPlayerStateManager.saveState(player)

            if (!lobby.sidebar.closed()) {
                lobby.sidebar.addPlayer(player)
            }

            lobby.teleportToLobby(player)
            player.gameMode = GameMode.ADVENTURE
            player.health = 20.0
            player.saturation = 20.0f
            player.inventory.clear()

            player.setHotbarItem(0, LobbyItems.colorSelector(player))

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

        globalEventBus.on<LobbyStartedEvent> {
            lobby.players().forEach { it.bukkitPlayer.inventory.clear() }

            lobby.players().forEach { player ->
                player.bukkitPlayer.gameMode = GameMode.CREATIVE
                player.bukkitPlayer.isFlying = true
                lobby.teleportToMap(player.bukkitPlayer)
            }
        }
    }
}
