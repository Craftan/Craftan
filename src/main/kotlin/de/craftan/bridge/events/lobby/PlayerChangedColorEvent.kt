package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.CraftanEvent
import org.bukkit.entity.Player
import java.awt.Color

data class PlayerChangedColorEvent(val player: Player, val lobby: CraftanLobby, val from: Color, val to: Color): CraftanEvent