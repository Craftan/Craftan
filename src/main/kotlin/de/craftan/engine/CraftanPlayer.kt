package de.craftan.engine

import de.craftan.engine.structures.CraftanStructure
import de.craftan.io.*
import de.craftan.util.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * Model a player inside a CraftanGame
 * @see CraftanGame
 */
interface CraftanPlayer {
    val game: CraftanGame

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
interface CraftanPlayerInventory {
    /**
     * Maps the resource to the amount
     */
    val resources: MutableMap<CraftanResource, Int>

    /**
     * Maps the structure to the amount
     */
    val structures: MutableMap<CraftanStructure, Int>

    /**
     * Maps the action card to the amount
     */
    val actionCards: Map<CraftanActionCard, Int>

    fun containsAtleastOne(neededResources: Map<CraftanResource, Int>): Boolean {
        for (neededResource in neededResources) {
            if (resources[neededResource.key]!! >= neededResource.value) return false
        }
        return true
    }

    fun add(resource: CraftanResource, amount:Int) {
        resources[resource] = resources[resource]!! + amount
    }

    fun remove(toRemove: Map<CraftanResource, Int>) {
        for (resource in toRemove) {
            resources[resource.key] = resources[resource.key]!! - resource.value
        }
    }
    fun containsAtleastOne(neededResource: CraftanStructure): Boolean {
        if (structures[neededResource]!! >= 1) return false
        return true
    }

    fun remove(toRemove: CraftanStructure) {
        structures[toRemove] = structures[toRemove]!! - 1
    }

}
