package de.craftan.bridge.lobby.scoreboard

import de.craftan.bridge.lobby.CraftanLobby
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.entity.Player

interface ScoreboardProvider {
    fun build(player: Player, lobby: CraftanLobby): SidebarComponent
    fun buildTitle(player: Player, lobby: CraftanLobby): Component
}
