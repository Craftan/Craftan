package de.craftan.bridge.items

import de.craftan.bridge.lobby.CraftanLobbyManager
import de.craftan.io.CraftanNotification
import de.craftan.io.resolve
import de.staticred.kia.inventory.builder.kItem
import de.staticred.kia.util.RuntimeFunction
import de.staticred.kia.inventory.extensions.openInventory
import org.bukkit.Material
import org.bukkit.entity.Player

@OptIn(RuntimeFunction::class)
object LobbyItems {

    fun colorSelector(player: Player) = kItem(Material.WHITE_WOOL) {
        setDisplayName(CraftanNotification.LOBBY_ITEMS_COLOR_SELECTOR.resolve(player))
        onRightClick { player, _ ->
            val lobby = CraftanLobbyManager.getLobbyForPlayer(player)
            if (lobby != null) {
                val inv = lobby.getSharedColorSelectorInventory(player)
                player.openInventory(inv)
            }
        }
    }
}