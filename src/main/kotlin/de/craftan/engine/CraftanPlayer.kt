package de.craftan.engine

import de.craftan.engine.structures.CraftanStructure
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.resolve
import de.craftan.io.resolveWithPlaceholder
import de.craftan.util.CraftanPermissions
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Model a player inside a CraftanGame
 * @see CraftanGame
 */
interface CraftanPlayer {
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
     * World the player is joining from
     */
    val origin: Location

    /**
     * Contains the inventory the player currently has
     */
    val inventory: CraftanPlayerInventory

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

/**
 * Models the inventory of resources, structures and cards a player current possesses
 */
data class CraftanPlayerInventory(
    /**
     * Maps the resource to the amount
     */
    val resources: Map<CraftanResource, Int> = emptyMap(),

    /**
     * Maps the structure to the amount
     */
    val structures: Map<CraftanStructure, Int> = emptyMap(),

    /**
     * Maps the action card to the amount
     */
    val actionCards: Map<CraftanActionCard, Int> = emptyMap(),
)
