package de.craftan.bridge.lobby.scoreboard

import de.craftan.Craftan
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.bridge.lobby.CraftanLobbyStatus
import de.craftan.bridge.util.resolveMiniMessage
import de.craftan.engine.resources.CraftanResourceType
import de.craftan.io.CraftanPlaceholder
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.entity.Player

class ConfigurableScoreboard : ScoreboardProvider {

    override fun build(player: Player, lobby: CraftanLobby): SidebarComponent {
        val config = Craftan.configs.scoreboards()
        val statusKey = when (lobby.status) {
            CraftanLobbyStatus.WAITING -> "waiting"
            CraftanLobbyStatus.STARTING -> "starting"
            CraftanLobbyStatus.IN_GAME -> "ingame"
            CraftanLobbyStatus.ENDED -> "ended"
        }

        val definition = config.scoreboards[statusKey] ?: config.scoreboards["waiting"]!!

        val placeholders = createPlaceholders(player, lobby)

        val builder = SidebarComponent.builder()
        definition.lines.forEach { line ->
            if (line.isEmpty()) {
                builder.addBlankLine()
            } else {
                builder.addDynamicLine { resolveLine(line, placeholders) }
            }
        }

        return builder.build()
    }

    override fun buildTitle(player: Player, lobby: CraftanLobby): Component {
        val config = Craftan.configs.scoreboards()
        val statusKey = when (lobby.status) {
            CraftanLobbyStatus.WAITING -> "waiting"
            CraftanLobbyStatus.STARTING -> "starting"
            CraftanLobbyStatus.IN_GAME -> "ingame"
            CraftanLobbyStatus.ENDED -> "ended"
        }
        val definition = config.scoreboards[statusKey] ?: config.scoreboards["waiting"]!!
        return resolveLine(definition.title, createPlaceholders(player, lobby))
    }

    private fun resolveLine(line: String, placeholders: Map<CraftanPlaceholder, String>): Component {
        var resolved = line
        placeholders.forEach { (placeholder, value) ->
            resolved = resolved.replace(placeholder.placeholder, value)
        }
        return resolved.resolveMiniMessage()
    }

    private fun createPlaceholders(player: Player, lobby: CraftanLobby): Map<CraftanPlaceholder, String> {
        val placeholders = mutableMapOf<CraftanPlaceholder, String>()
        placeholders[CraftanPlaceholder.PLAYER] = player.name
        placeholders[CraftanPlaceholder.LOBBY_ID] = lobby.id.toString()
        placeholders[CraftanPlaceholder.CURRENT_PLAYERS] = lobby.players().size.toString()
        placeholders[CraftanPlaceholder.MAX_PLAYERS] = lobby.maxPlayers.toString()
        placeholders[CraftanPlaceholder.CURRENT_MAP] = lobby.board.layout.name
        placeholders[CraftanPlaceholder.LOBBY_STATUS] = lobby.status.name
        placeholders[CraftanPlaceholder.STATUS] = lobby.status.name
        placeholders[CraftanPlaceholder.LOBBY_COUNTDOWN] = if (lobby.countingDown) lobby.countdown.toString() else "0"
        placeholders[CraftanPlaceholder.PLAYER_COLOR] = lobby.craftanPlayer(player)?.team?.name ?: ""

        val craftanPlayer = lobby.players().find { it.bukkitPlayer.uniqueId == player.uniqueId }
        val game = craftanPlayer?.game
        if (game != null) {
            val state = game.stateHandler.state
            val enginePlayer = game.gameFlow.players.find { it.name == player.name }
            if (enginePlayer != null) {
                placeholders[CraftanPlaceholder.VICTORY_POINTS] = (state.winPoints[enginePlayer] ?: 0).toString()
                val playerResources = state.resources[enginePlayer]
                placeholders[CraftanPlaceholder.RESOURCES] = (playerResources?.values?.sum() ?: 0).toString()
                placeholders[CraftanPlaceholder.RESOURCES_LUMBER] = (playerResources?.get(CraftanResourceType.LUMBER) ?: 0).toString()
                placeholders[CraftanPlaceholder.RESOURCES_WOOL] = (playerResources?.get(CraftanResourceType.WOOL) ?: 0).toString()
                placeholders[CraftanPlaceholder.RESOURCES_GRAIN] = (playerResources?.get(CraftanResourceType.GRAIN) ?: 0).toString()
                placeholders[CraftanPlaceholder.RESOURCES_BRICK] = (playerResources?.get(CraftanResourceType.BRICK) ?: 0).toString()
                placeholders[CraftanPlaceholder.RESOURCES_ORE] = (playerResources?.get(CraftanResourceType.ORE) ?: 0).toString()
            } else {
                placeholders[CraftanPlaceholder.VICTORY_POINTS] = "0"
                placeholders[CraftanPlaceholder.RESOURCES] = "0"
                placeholders[CraftanPlaceholder.RESOURCES_LUMBER] = "0"
                placeholders[CraftanPlaceholder.RESOURCES_WOOL] = "0"
                placeholders[CraftanPlaceholder.RESOURCES_GRAIN] = "0"
                placeholders[CraftanPlaceholder.RESOURCES_BRICK] = "0"
                placeholders[CraftanPlaceholder.RESOURCES_ORE] = "0"
            }
            placeholders[CraftanPlaceholder.ROUND] = (game.gameFlow.roundIndex).toString()
            val currentTurnPlayerName = game.gameFlow.round?.currentTurn()?.first?.name ?: "N/A"
            placeholders[CraftanPlaceholder.TURN_PLAYER] = currentTurnPlayerName
            placeholders[CraftanPlaceholder.CURRENT_TURN] = currentTurnPlayerName
        } else {
            placeholders[CraftanPlaceholder.VICTORY_POINTS] = "0"
            placeholders[CraftanPlaceholder.RESOURCES] = "0"
            placeholders[CraftanPlaceholder.RESOURCES_LUMBER] = "0"
            placeholders[CraftanPlaceholder.RESOURCES_WOOL] = "0"
            placeholders[CraftanPlaceholder.RESOURCES_GRAIN] = "0"
            placeholders[CraftanPlaceholder.RESOURCES_BRICK] = "0"
            placeholders[CraftanPlaceholder.RESOURCES_ORE] = "0"
            placeholders[CraftanPlaceholder.ROUND] = "0"
            placeholders[CraftanPlaceholder.TURN_PLAYER] = "N/A"
            placeholders[CraftanPlaceholder.CURRENT_TURN] = "N/A"
        }

        return placeholders
    }
}
