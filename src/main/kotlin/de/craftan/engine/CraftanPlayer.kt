package de.craftan.engine

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
}

/**
 * Models the inventory of resources, structures and cards a player current possesses
 */
interface CraftanPlayerInventory {
    /**
     * Maps the resource to the amount
     */
    val resources: Map<CraftanResource, Int>

    /**
     * Maps the structure to the amount
     */
    val structures: Map<CraftanStructure, Int>

    /**
     * Maps the action card to the amount
     */
    val actionCards: Map<CraftanActionCard, Int>
}
