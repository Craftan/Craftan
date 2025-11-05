package de.craftan.bridge.inventory.config

import de.craftan.bridge.inventory.placeholderItem
import de.craftan.bridge.inventory.placeholderRow
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.MessageAdapter
import de.craftan.io.resolve
import de.craftan.io.resolveWithPlaceholder
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

private val timeToRollOptions = listOf(
    30, 60, 90
)

fun configureCraftanGameInventory(player: Player) = kInventory(player, 5.rows, InventoryType.CHEST) {
    val contrastColor = MessageAdapter.resolveMessage("base_highlight", player.locale().toString())
    title = CraftanNotification.LOBBY_CONFIG_INVENTORY_TITLE.resolve(player)

    CraftanGameConfigManager.insertPlayerWithDefaultConfigIfNotExisting(player)
    val config = CraftanGameConfigManager.getGameConfig(player)!!


    setRow(0, placeholderRow)

    setRow(1, kRow {
        setItem(0, placeholderItem)
        setItem(1, kItem(Material.SPRUCE_BOAT) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_EXTENSION_OPTION.resolve(player))
        })
        setItem(2, placeholderItem)
        setItem(3..8, kItem(Material.BARRIER) {
            setDisplayName(literalText("Coming soon"))
        })
    })

    setRow(2, kRow {
        setItem(0, placeholderItem)
        setItem(1, kItem(Material.CLOCK) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_TIME_DICE_OPTION.resolve(player))
        })
        setItem(2, placeholderItem)

        timeToRollOptions.forEachIndexed { index, time ->
            setItem(3 + index, kItem(Material.CLOCK) {
                val isSelected = time == config.timeToRollDice
                val timeComponent = getTimeComponent(player, time, isSelected)

                if (isSelected) {
                    toItemStack().addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1)
                }

                setDisplayName(contrastColor.append(timeComponent))
                this.onClick { item, _ ->
                    config.timeToRollDice = time

                    getRowFor(2).items.forEach { (index, item) ->
                        item.setDisplayName(getTimeComponent(player, time, false))
                        item.toItemStack().removeEnchantments()
                        this@kRow.setItem(index, item)
                    }

                    item.setDisplayName(getTimeComponent(player, time, true))
                    item.toItemStack().addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1)
                    this@kRow.setItem(3 + index, item)
                }
            })
        }

        setItem(6..8, placeholderItem)
    })

    setRow(3, kRow {
        setItem(0, placeholderItem)
        setItem(1, kItem(Material.GOLD_INGOT) {
            setDisplayName(literalText("Points to win"))
        })

        setItem(3, kItem(Material.GREEN_TERRACOTTA) {
            setDisplayName(literalText("+1"))

            onClick { _, _ ->
                config.pointsToWin += 1
                this@kRow.setItem(4, pointsToWinItem(config.pointsToWin))
            }
        })

        setItem(4, pointsToWinItem(config.pointsToWin))

        setItem(5, kItem(Material.RED_TERRACOTTA) {
            setDisplayName(literalText("-1"))

            onClick { _, _ ->
                config.pointsToWin -= 1
                this@kRow.setItem(4, pointsToWinItem(config.pointsToWin))
            }
        })

        setItem(7, placeholderItem)
    })

    setRow(4, kRow {
        setItem(0..8, placeholderItem)
        setItem(7, kItem(Material.GREEN_WOOL) {
            setDisplayName(literalText("Create lobby"))
        })
    })
}

private fun pointsToWinItem(pointsToWin: Int) = kItem(Material.PAPER, pointsToWin) {
    setDisplayName(literalText("$pointsToWin Points to win")) }


private fun getTimeComponent(player: Player, time: Int, isSelected: Boolean) =  CraftanNotification.LOBBY_CONFIG_INVENTORY_TIME_DICE.resolveWithPlaceholder(player, mapOf(
    CraftanPlaceholder.DICE_TIME to literalText(time.toString()),
    CraftanPlaceholder.OPTION_SELECTED to if (isSelected) CraftanNotification.LOBBY_CONFIG_INVENTORY_OPTION_SELECTED.resolve(player) else literalText()
))