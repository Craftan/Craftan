package de.craftan.game

import de.staticred.kia.inventory.builder.kItem
import de.staticred.kia.inventory.item.RegisteredKItem
import org.bukkit.Material

open class CraftanItem(
    material: Material,
    amount: Int,
    val player: CraftanPlayer,
) {
    val kItem = kItem(material, amount)
}

/**
 * Craftan Action Items represent the connection between the player in the game.
 * This item allows the player to interact by executing the given action when right-clicking the item ingame
 *
 * Use [craftanActionItem] as an easier builder
 *
 * @param material the material
 * @param amount the amount of the item in the inventory
 * @param player the player which holds this item
 * @param action the action to execute
 */
class CraftanActionItem<R>(
    material: Material,
    amount: Int,
    player: CraftanPlayer,
    private val action: CraftanGameAction<R>,
) : CraftanItem(material, amount, player) {
    init {
        kItem.onRightClick { _, _ ->
            action.invoke(player)
        }
    }
}

/**
 * Builds a new craftan action item
 *
 * @param material the material
 * @param amount the amount in the inventory
 * @param player the player holding this item
 * @param action the action executed when the item is right-clicked
 * @param block configuration of the kItem
 * @see CraftanActionItem
 */
fun <R> craftanActionItem(
    material: Material,
    amount: Int,
    player: CraftanPlayer,
    action: CraftanGameAction<R>,
    block: RegisteredKItem.() -> Unit = {},
): CraftanActionItem<R> {
    val item = CraftanActionItem<R>(material, amount, player, action)
    item.kItem.apply(block)
    return item
}
