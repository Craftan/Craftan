package de.craftan.bridge.lobby

import de.craftan.engine.CraftanPlayer
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.commands.to
import net.kyori.adventure.text.Component

/**
 * Models a lobby which holds the players and the ongoing game.
 * @param board the board the game is participating on
 */
class CraftanLobby(
    val board: CraftanBoard,
    val settings: MutableCraftanSettings,
    val maxPlayers: Int = 4
) {
    private val players = mutableListOf<CraftanPlayer>()

    fun addPlayer(player: CraftanPlayer) {
        players += player
        notifyPlayers(CraftanNotification.JOINED_GAME, mapOf(CraftanPlaceholder.PLAYER to player.bukkitPlayer.name))
    }

    fun removePlayer(player: CraftanPlayer) {
        players -= player
        notifyPlayers(CraftanNotification.LEFT_GAME, mapOf(CraftanPlaceholder.PLAYER to player.bukkitPlayer.name))
    }

    fun notifyPlayers(notification: CraftanNotification) {
        players.forEach { it.sendNotification(notification) }
    }

    fun notifyPlayers(
        notification: CraftanNotification,
        placeholders: Map<CraftanPlaceholder, Component>,
    ) {
        players.forEach { it.sendNotification(notification, placeholders) }
    }
}
