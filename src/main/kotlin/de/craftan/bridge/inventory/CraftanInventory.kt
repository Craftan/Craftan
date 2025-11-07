package de.craftan.bridge.inventory

import de.staticred.kia.inventory.builder.kItem
import de.staticred.kia.inventory.builder.kRow
import net.axay.kspigot.chat.literalText
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material

val placeholderItem = kItem(Material.BLACK_STAINED_GLASS_PANE, 1) {
    setDisplayName(literalText("|").color(TextColor.color(0)))
}

val placeholderRow = kRow {
    setItem(0..9, placeholderItem)
}