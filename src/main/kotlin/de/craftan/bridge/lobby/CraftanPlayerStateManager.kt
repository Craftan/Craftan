package de.craftan.bridge.lobby

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object CraftanPlayerStateManager {

    private val playerState = mutableMapOf<Player, PlayerState>()

    fun saveState(player: Player) {
        playerState[player] = PlayerState(
            player.inventory,
            player.health,
            player.saturation,
            player.location,
            player.gameMode
        )
    }

    fun applyState(player: Player) {
        val state = playerState[player] ?: return

        player.apply {
            inventory.contents = state.inventory.contents
            health = state.health
            saturation = state.hunger
            teleport(state.position)
            gameMode = state.gameMode
        }

        playerState.remove(player)
    }
}

data class PlayerState(
    val inventory: Inventory,
    val health: Double,
    val hunger: Float,
    val position: Location,
    val gameMode: GameMode,
)