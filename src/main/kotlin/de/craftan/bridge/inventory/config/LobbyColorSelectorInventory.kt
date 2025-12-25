package de.craftan.bridge.inventory.config

import de.craftan.Craftan
import de.craftan.bridge.inventory.placeholderRow
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.bridge.lobby.CraftanLobbyManager
import de.craftan.bridge.lobby.CraftanLobbyStatus
import de.craftan.io.CraftanNotification
import de.craftan.io.resolve
import de.staticred.kia.inventory.KRow
import de.staticred.kia.inventory.builder.kInventory
import de.staticred.kia.inventory.builder.kItem
import de.staticred.kia.inventory.builder.kRow
import de.staticred.kia.util.rows
import net.axay.kspigot.chat.literalText
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import java.awt.Color

private val colorsToItems = Craftan.configs.gameSettings()
    .colors
    .mapValues { ColorItem(Color(it.value.color), Material.valueOf(it.value.resource)) }

fun colorSelectorInventory(player: Player, lobby: CraftanLobby) = kInventory(player, 3.rows, InventoryType.CHEST) {
    title = CraftanNotification.LOBBY_COLOR_SELECTOR_INVENTORY_TITLE.resolve(player)
    setRow(0, placeholderRow)
    setRow(1, colorRow(player, lobby))
    setRow(2, placeholderRow)
}

private fun colorRow(player: Player, lobby: CraftanLobby): KRow = kRow {
    colorsToItems.entries.forEachIndexed { index, (colorName, item) ->
        setItem(index, selectColorItem(player, colorName, item, lobby))
    }
}

private fun selectColorItem(player: Player, colorName: String, colorItem: ColorItem, lobby: CraftanLobby) = kItem(colorItem.material) {
    val color = colorItem.color

    val available = !lobby.hasPlayerColor(color)
    val itemName = if (available) colorName else "USED - $colorName"
    setDisplayName(literalText(itemName).color(TextColor.color(color.rgb)))

    val isPlayerUsingColor =  lobby.hasPlayerColor(color) && lobby.getPlayerColor(player) == color

    if (isPlayerUsingColor) {
        toItemStack().addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1)
    }

    if (available) {
        onClick { _, player ->
            val lobby = CraftanLobbyManager.getLobbyForPlayer(player) ?: return@onClick
            if (lobby.status != CraftanLobbyStatus.WAITING && lobby.status != CraftanLobbyStatus.STARTING) return@onClick

            val hasPlayerColor = lobby.hasPlayerColor(color)

            if (!hasPlayerColor) {
                lobby.setPlayerColor(player, color)
                player.sendMessage("You selected the $colorName color.")
                setRow(1, colorRow(player, lobby))
            }
        }
    }
}

data class ColorItem(val color: Color, val material: Material)