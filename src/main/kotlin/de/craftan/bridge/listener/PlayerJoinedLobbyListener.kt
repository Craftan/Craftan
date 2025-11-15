package de.craftan.bridge.listener

import de.craftan.bridge.events.lobby.PlayerJoinedLobbyEvent
import de.craftan.bridge.lobby.CraftanLobbyManager
import net.ormr.eventbus.Subscribed

class PlayerJoinedLobbyListener {

    init {
    }

    @Subscribed
    fun onPlayerJoined(event: PlayerJoinedLobbyEvent) {
        println(event)
    }
}