package de.craftan.bridge.inventory.config

import de.craftan.bridge.inventory.placeholderItem
import de.craftan.bridge.lobby.LobbyManager
import de.craftan.bridge.util.sendNotification
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.MutableCraftanGameConfig
import de.craftan.engine.map.maps.DefaultMapLayout
import de.craftan.io.*
import de.staticred.kia.inventory.KRow
import de.staticred.kia.inventory.builder.kInventory
import de.staticred.kia.inventory.builder.kItem
import de.staticred.kia.inventory.builder.kRow
import de.staticred.kia.util.rows
import net.axay.kspigot.chat.literalText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

private val timeToRollOptions = listOf(
    30, 60, 90
)

private val turnTimeOptions = listOf(
    60, 90, 120
)

fun configureCraftanGameInventory(player: Player) = kInventory(player, 6.rows, InventoryType.CHEST) {
    val contrastColor = MessageAdapter.resolveMessage("base_highlight", player.locale().toString())
    title = CraftanNotification.LOBBY_CONFIG_INVENTORY_TITLE.resolve(player)

    CraftanGameConfigManager.insertPlayerWithDefaultConfigIfNotExisting(player)
    val config = CraftanGameConfigManager.getGameConfig(player)!!

    onClose {
        if (CraftanGameConfigManager.getGameConfig(player) != null) {
            player.sendNotification(CraftanNotification.LOBBY_CONFIG_INVENTORY_CLOSED)
        }
    }

    setRow(0, kRow {
        setItem(0, placeholderItem)
        setItem(1, kItem(Material.SPRUCE_BOAT) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_EXTENSION_OPTION.resolve(player))
        })
        setItem(2, placeholderItem)
        setItem(3..8, kItem(Material.BARRIER) {
            setDisplayName(literalText("Coming soon"))
        })
    })

    setRow(1, getDiceTimeRow(player, config, contrastColor))

    setRow(2, getTurnTimeRow(player, config, contrastColor))

    setRow(3, kRow {
        setItem(0, placeholderItem)
        setItem(1, kItem(Material.GOLD_INGOT) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_POINTS_TO_WIN_OPTION.resolve(player))
        })

        setItem(2, placeholderItem)

        setItem(3, kItem(Material.GREEN_TERRACOTTA) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_POINTS_TO_WIN_PLUS.resolve(player))

            onClick { _, _ ->
                // TODO change this based on extensions configuration
                if (config.pointsToWin >= 14) return@onClick
                config.pointsToWin += 1
                this@kRow.setItem(4, pointsToWinItem(config.pointsToWin, player))
            }
        })

        setItem(4, pointsToWinItem(config.pointsToWin, player))

        setItem(5, kItem(Material.RED_TERRACOTTA) {
            setDisplayName(literalText("-1"))

            onClick { _, _ ->
                if (config.pointsToWin <= 7) return@onClick
                config.pointsToWin -= 1
                this@kRow.setItem(4, pointsToWinItem(config.pointsToWin, player))
            }
        })

        setItem(6..8, placeholderItem)
    })

    setRow(4, kRow {
        setItem(0, placeholderItem)

        setItem(1, kItem(Material.BARRIER) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_CARDS_LIMIT_OPTION.resolve(player))
        })

        setItem(2, placeholderItem)


        setItem(3, kItem(Material.GREEN_TERRACOTTA) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_CARDS_LIMIT_PLUS.resolve(player))

            onClick { _, _ ->
                // TODO change this based on extensions configuration
                if (config.cardsLimit >= 14) return@onClick
                config.cardsLimit += 1
                this@kRow.setItem(4, cardsLimitItem(config.cardsLimit, player))
            }
        })

        setItem(4, cardsLimitItem(config.cardsLimit, player))

        setItem(5, kItem(Material.RED_TERRACOTTA) {
            setDisplayName(literalText("-1"))

            onClick { _, _ ->
                if (config.cardsLimit <= 7) return@onClick
                config.cardsLimit -= 1
                this@kRow.setItem(4, cardsLimitItem(config.cardsLimit, player))
            }
        })

        setItem(6..8, placeholderItem)
    })

    setRow(5, kRow {
        setItem(0..8, placeholderItem)
        setItem(8, kItem(Material.GREEN_WOOL) {
            setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_CREATE_LOBBY.resolve(player))

            onClick { _, _ ->
                createLobby(player, config.toCraftanGameConfig())
            }
        })
    })
}

private fun createLobby(player: Player, config: CraftanGameConfig) {
    if (LobbyManager.isInLobby(player)) {
        player.closeInventory()
        player.sendNotification(CraftanNotification.ALREADY_IN_LOBBY)
        return
    }

    val lobby = LobbyManager.createLobby(DefaultMapLayout(), config)
    lobby.addPlayer(player)

    CraftanGameConfigManager.clearPlayer(player)

    player.closeInventory()
    player.sendNotification(CraftanNotification.LOBBY_CREATED)
}

private fun pointsToWinItem(pointsToWin: Int, player: Player) = kItem(Material.PAPER, pointsToWin) {
    setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_POINTS_TO_WIN.resolveWithPlaceholder(player, mapOf(
        CraftanPlaceholder.POINTS_TO_WIN to literalText(pointsToWin.toString())
    ))) }

private fun cardsLimitItem(cardsLimit: Int, player: Player) = kItem(Material.PAPER, cardsLimit) {
    setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_CARDS_LIMIT.resolveWithPlaceholder(player, mapOf(
        CraftanPlaceholder.CARDS_LIMIT to literalText(cardsLimit.toString())
    ))) }


private fun getDiceTimeComponent(player: Player, time: Int, isSelected: Boolean) =  CraftanNotification.LOBBY_CONFIG_INVENTORY_TIME_DICE.resolveWithPlaceholder(player, mapOf(
    CraftanPlaceholder.DICE_TIME to literalText(time.toString()),
    CraftanPlaceholder.OPTION_SELECTED to if (isSelected) CraftanNotification.LOBBY_CONFIG_INVENTORY_OPTION_SELECTED.resolve(player) else literalText()
))

private fun getDiceTimeRow(player: Player, config: MutableCraftanGameConfig, contrastColor: Component): KRow = kRow {
    setItem(0, placeholderItem)
    setItem(1, kItem(Material.CLOCK) {
        setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_TIME_DICE_OPTION.resolve(player))
    })
    setItem(2, placeholderItem)

    timeToRollOptions.forEachIndexed { index, time ->
        setItem(3 + index, kItem(Material.CLOCK) {
            val isSelected = time == config.timeToRollDice
            val timeComponent = getDiceTimeComponent(player, time, isSelected)

            if (isSelected) {
                toItemStack().addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1)
            }

            setDisplayName(contrastColor.append(timeComponent))
            this.onClick { item, _ ->
                config.timeToRollDice = time

                setRow(1, getDiceTimeRow(player, config, contrastColor))

                item.setDisplayName(getDiceTimeComponent(player, time, true))
                item.toItemStack().addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1)
                this@kRow.setItem(3 + index, item)
            }
        })
    }

    setItem(6..8, placeholderItem)

}

private fun getTurnTimeRow(player: Player, config: MutableCraftanGameConfig, contrastColor: Component): KRow = kRow {
    setItem(0, placeholderItem)
    setItem(1, kItem(Material.CLOCK) {
        setDisplayName(CraftanNotification.LOBBY_CONFIG_INVENTORY_TIME_TURN_OPTION.resolve(player))
    })
    setItem(2, placeholderItem)

    turnTimeOptions.forEachIndexed { index, time ->
        setItem(3 + index, kItem(Material.CLOCK) {
            val isSelected = time == config.timeToFinishTurn
            val timeComponent = getTurnTimeComponent(player, time, isSelected)

            if (isSelected) {
                toItemStack().addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1)
            }

            setDisplayName(contrastColor.append(timeComponent))
            this.onClick { item, _ ->
                config.timeToFinishTurn = time

                setRow(2, getTurnTimeRow(player, config, contrastColor))

                item.setDisplayName(getTurnTimeComponent(player, time, true))
                item.toItemStack().addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1)
                this@kRow.setItem(3 + index, item)
            }
        })
    }

    setItem(6..8, placeholderItem)
}

private fun getTurnTimeComponent(player: Player, time: Int, isSelected: Boolean) =  CraftanNotification.LOBBY_CONFIG_INVENTORY_TIME_TURN.resolveWithPlaceholder(player, mapOf(
    CraftanPlaceholder.TURN_TIME to literalText(time.toString()),
    CraftanPlaceholder.OPTION_SELECTED to if (isSelected) CraftanNotification.LOBBY_CONFIG_INVENTORY_OPTION_SELECTED.resolve(player) else literalText()
))