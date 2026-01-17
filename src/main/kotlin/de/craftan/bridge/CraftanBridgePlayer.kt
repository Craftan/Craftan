package de.craftan.bridge

import de.craftan.engine.resources.CraftanActionCard
import de.craftan.engine.CraftanGame
import de.craftan.engine.structures.CraftanStructure
import de.craftan.io.*
import de.craftan.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.awt.Color

/**
 * Model a player inside a CraftanGame
 * @see de.craftan.engine.CraftanGame
 */
interface CraftanBridgePlayer {
    val game: CraftanGame?

    /**
     * The actual online player
     */
    val bukkitPlayer: Player

    /**
     * Amount of victory the player has
     */
    val victoryPoints: Int

    /**
     * Contains the inventory the player currently has
     */
    val inventory: CraftanPlayerInventory

    var team: CraftanTeam?

    /**
     * Send the given component to the ingame player
     * @param component the text
     */
    fun sendMessage(component: Component) {
        bukkitPlayer.sendMessage(component)
    }

    /**
     * Send the notification to the player
     * @param notification
     */
    fun sendNotification(notification: CraftanNotification) {
        bukkitPlayer.sendMessage(notification.resolve(bukkitPlayer))
    }

    /**
     * Send the notification to the player with the given placeholders
     * @param notification to send
     * @param placeholders to replace with
     * @see resolveWithPlaceholder
     */
    fun sendNotification(
        notification: CraftanNotification,
        placeholders: Map<CraftanPlaceholder, Component>,
    ) {
        bukkitPlayer.sendMessage(notification.resolveWithPlaceholder(bukkitPlayer, placeholders))
    }

    /**
     * @param permission the permission
     * @return true if player has the permission
     */
    fun hasPermission(permission: String): Boolean = bukkitPlayer.hasPermission(permission)

    /**
     * @param permission the permission
     * @return true if player has the permission
     */
    fun hasPermission(permission: CraftanPermissions): Boolean = hasPermission(permission.permission)
}

data class CraftanTeam(
    val color: NamedTextColor,
    val name: String
)

/**
 * Models the inventory of resources, structures and cards a player current possesses
 */
data class CraftanPlayerInventory(
    /**
     * Maps the resource to the amount
     */
    val resources: Map<CraftanBridgeResource, Int> = emptyMap(),

    /**
     * Maps the structure to the amount
     */
    val structures: Map<CraftanStructure, Int> = emptyMap(),

    /**
     * Maps the action card to the amount
     */
    val actionCards: Map<CraftanActionCard, Int> = emptyMap(),
)
