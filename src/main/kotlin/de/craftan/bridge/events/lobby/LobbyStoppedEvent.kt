package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.CraftanEvent

data class LobbyStoppedEvent(val lobby: CraftanLobby): CraftanEvent
