package de.craftan.bridge.lobby

import net.axay.kspigot.extensions.server
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

object CraftanTeamManager {

    private val lobbyScoreboards = mutableMapOf<CraftanLobby, Scoreboard>()

    context(lobby: CraftanLobby)
    fun assignPlayerToTeam(player: Player) {
        val color = lobby.getPlayerColor(player) ?: return

        if (!lobbyScoreboards.containsKey(lobby)) {
            lobbyScoreboards[lobby] = server.scoreboardManager.newScoreboard
        }

        val scoreboard = lobbyScoreboards[lobby]!!

        val teamName = "lobby-${lobby.id}-${color.rgb}"
        val team = scoreboard.getTeam(teamName) ?: scoreboard.registerNewTeam(teamName)

        team.addPlayer(player)
        team.color(NamedTextColor.namedColor(color.rgb))
    }
}