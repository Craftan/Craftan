package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.CraftanEvent
import org.bukkit.entity.Player

data class PlayerLeftLobbyEvent(val lobby: CraftanLobby, val player: Player): CraftanEvent

data class PlayerSoftLeftLobbyEvent(val lobby: CraftanLobby, val player: Player): CraftanEvent