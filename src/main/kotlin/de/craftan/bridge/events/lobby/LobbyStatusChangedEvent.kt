package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.bridge.lobby.CraftanLobbyStatus
import de.craftan.io.CraftanEvent

data class LobbyStatusChangedEvent(val lobby: CraftanLobby, val from: CraftanLobbyStatus): CraftanEvent