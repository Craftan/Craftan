package de.craftan.bridge.lobby

import de.craftan.engine.CraftanPlayer
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholders

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
        notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholders.PLAYER to player.bukkitPlayer.name))
    }

    fun removePlayer(player: CraftanPlayer) {
        players -= player
        notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholders.PLAYER to player.bukkitPlayer.name))
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun notifyPlayers(
        notification: CraftanNotification,
        placeholders: Map<CraftanPlaceholders, String>,
    ) {
        players.forEach { it.sendNotification(notification, placeholders) }
    }
}
