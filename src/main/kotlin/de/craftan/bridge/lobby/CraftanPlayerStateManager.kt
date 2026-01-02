package de.craftan.bridge.lobby

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID

object CraftanPlayerStateManager {

    private val playerState = mutableMapOf<UUID, PlayerState>()

    fun saveState(player: Player) {
        playerState[player.uniqueId] = PlayerState(
            player.inventory.contents,
            player.health,
            player.foodLevel,
            player.location,
            player.gameMode
        )
    }

    fun applyState(player: Player) {
        val state = playerState[player.uniqueId] ?: return

        player.apply {
            inventory.contents = state.inventory
            health = state.health
            foodLevel = state.foodLevel
            teleport(state.position)
            gameMode = state.gameMode
        }

        playerState.remove(player.uniqueId)
    }
}

data class PlayerState(
    val inventory: Array<out ItemStack?>,
    val health: Double,
    val foodLevel: Int,
    val position: Location,
    val gameMode: GameMode,
)