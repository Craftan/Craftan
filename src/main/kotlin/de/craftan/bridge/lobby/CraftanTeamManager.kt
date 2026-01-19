import de.craftan.bridge.lobby.CraftanLobby
import net.axay.kspigot.extensions.server
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

object CraftanTeamManager {

    const val TEAM_PREFIX = "craftan-team-"

    context(lobby: CraftanLobby)
    fun assignPlayerToTeam(player: Player): Team {
        val color = lobby.getPlayerColor(player) ?: throw IllegalStateException("Player ${player.name} has no color assigned!")

        val scoreboard = server.scoreboardManager.mainScoreboard

        val teamName = "${TEAM_PREFIX}lobby-${lobby.id}"
        val team = scoreboard.getTeam(teamName) ?: scoreboard.registerNewTeam(teamName)

        team.color(color)

        return team
    }

    fun deleteCraftanTeams() {
        server.scoreboardManager.mainScoreboard.teams.filter { it.name.startsWith(TEAM_PREFIX) }.forEach { it.unregister() }
    }
}