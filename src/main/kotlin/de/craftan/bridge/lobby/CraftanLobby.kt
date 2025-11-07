package de.craftan.bridge.lobby

import de.craftan.Craftan
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.implementations.CraftanPlayerImpl
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.commands.to
import net.axay.kspigot.chat.literalText
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.entity.Player

/**
 * Models a lobby which holds the players and the ongoing game.
 * @param board the board the game is participating on
 */
class CraftanLobby(
    val board: CraftanBoard,
    val gameConfig: CraftanGameConfig,
    val maxPlayers: Int = 4,
    val minPlayers: Int = 3,
    val status: CraftanLobbyStatus = CraftanLobbyStatus.WAITING
) {
    private val players = mutableListOf<CraftanPlayer>()
    private val sidebar = buildSidebar()

    fun addPlayer(player: Player) {
        val craftanPlayer = CraftanPlayerImpl(player)
        players += craftanPlayer
        notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))
        sidebar.addPlayer(player)
    }

    fun removePlayer(player: Player) {
        val craftanPlayer = CraftanPlayerImpl(player)
        players -= craftanPlayer
        notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))
        sidebar.removePlayer(player)
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun closeLobby() {
        sidebar.close()
    }

    fun players() = players.toList()

    fun notifyPlayers(
        notification: CraftanNotification,
        placeholders: Map<CraftanPlaceholder, Component>,
    ) {
        players.forEach { it.sendNotification(notification, placeholders) }
    }


    private fun buildSidebar(): Sidebar {
        val sidebar = Craftan.scoreboardLibrary.createSidebar()

        val lines = SidebarComponent.builder()
            .addBlankLine()
            .addStaticLine(literalText("Players: ${players().size}/${maxPlayers}"))
            .addStaticLine(literalText("Map: ${board.layout.name}"))
            .addStaticLine(literalText("Status: $status"))
            .addBlankLine()
            .build()

        val title = SidebarComponent.staticLine(literalText("Craftan"))

        ComponentSidebarLayout(title, lines).apply(sidebar)
        return sidebar
    }
}
