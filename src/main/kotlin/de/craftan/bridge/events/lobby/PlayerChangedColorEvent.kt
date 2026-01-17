package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.CraftanEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.awt.Color

data class PlayerChangedColorEvent(val player: Player, val lobby: CraftanLobby, val from: NamedTextColor, val to: NamedTextColor): CraftanEvent