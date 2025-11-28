package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.CraftanEvent

data class LobbyCountdownEvent(val lobby: CraftanLobby, val secondsLeft: Int): CraftanEvent
