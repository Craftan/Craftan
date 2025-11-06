package de.craftan.bridge.lobby

import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.implementations.CraftanPlayerImpl
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.commands.to
import net.kyori.adventure.text.Component
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

    fun addPlayer(player: Player) {
        val craftanPlayer = CraftanPlayerImpl(player)
        players += craftanPlayer
        notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))
    }

    fun removePlayer(player: Player) {
        val craftanPlayer = CraftanPlayerImpl(player)
        players += craftanPlayer
        notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to player.name))
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun players() = players.toList()

    fun notifyPlayers(
        notification: CraftanNotification,
        placeholders: Map<CraftanPlaceholder, Component>,
    ) {
        players.forEach { it.sendNotification(notification, placeholders) }
    }
}
