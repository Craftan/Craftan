package de.craftan.bridge.listener.bukkit

import de.craftan.bridge.lobby.CraftanLobbyManager
import de.craftan.bridge.lobby.CraftanLobbyStatus
import de.craftan.bridge.util.sendNotification
import de.craftan.io.CraftanNotification
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent


class PlayerJoinedLeftListener: CraftanBukkitListener {
    override fun register() {
        listen<PlayerQuitEvent> {
            val lobby = CraftanLobbyManager.getLobbyForPlayer(it.player) ?: return@listen

            if (lobby.players().size == 1) {
                lobby.close()
                return@listen
            }

            if (lobby.status == CraftanLobbyStatus.IN_GAME) {
                lobby.softRemovePlayer(it.player)
                return@listen
            }

            lobby.removePlayer(it.player)
        }

        listen<PlayerJoinEvent> {
            val lobby = CraftanLobbyManager.getLobbyForPlayer(it.player) ?: return@listen

            if (lobby.status == CraftanLobbyStatus.IN_GAME) {
                it.player.sendNotification(CraftanNotification.REJOIN_GAME)
                task(delay = 20*3) { _ ->
                    lobby.rejoinGame(it.player)
                }
            }
        }
    }
}