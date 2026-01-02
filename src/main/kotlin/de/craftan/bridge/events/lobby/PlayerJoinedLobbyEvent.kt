package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.CancellableCraftanEvent
import org.bukkit.entity.Player

class PlayerJoinedLobbyEvent(val lobby: CraftanLobby, val player: Player): CancellableCraftanEvent()

class PlayerRejoinedLobbyEvent(val lobby: CraftanLobby, val player: Player): CancellableCraftanEvent()