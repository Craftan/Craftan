package de.craftan.bridge.lobby

import de.craftan.engine.CraftanPlayer
import de.craftan.util.CraftanNotifications
import de.craftan.util.CraftanPlaceholders

/**
 * Models a lobby which holds the players and the ongoing game.
 * @param board the board the game is participating on
 */
class CraftanLobby(
    val board: CraftanBoard,
    val settings: MutableCraftanSettings,
) {
    private val players = mutableListOf<CraftanPlayer>()

    fun addPlayer(player: CraftanPlayer) {
        players += player
        notifyPlayers(CraftanNotifications.JOINED_GAME, mapOf(CraftanPlaceholders.PLAYER to player.bukkitPlayer.name))
    }

    fun removePlayer(player: CraftanPlayer) {
        players -= player
        notifyPlayers(CraftanNotifications.LEFT_GAME, mapOf(CraftanPlaceholders.PLAYER to player.bukkitPlayer.name))
    }

    fun notifyPlayers(notification: CraftanNotifications) {
        players.forEach { it.sendNotification(notification) }
    }

    fun notifyPlayers(
        notification: CraftanNotifications,
        placeholders: Map<CraftanPlaceholders, String>,
    ) {
        players.forEach { it.sendNotification(notification, placeholders) }
    }
}
