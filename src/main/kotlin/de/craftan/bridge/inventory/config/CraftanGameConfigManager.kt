package de.craftan.bridge.inventory.config

import de.craftan.engine.MutableCraftanGameConfig
import org.bukkit.entity.Player

object CraftanGameConfigManager {

    private val gameConfigMap = mutableMapOf<Player, MutableCraftanGameConfig>()

    fun insertPlayerWithDefaultConfigIfNotExisting(player: Player) {
        if (gameConfigMap.containsKey(player)) {
            return
        }

        gameConfigMap[player] = MutableCraftanGameConfig()
    }

    fun clearPlayer(player: Player) {
        gameConfigMap.remove(player)
    }

    fun getGameConfig(player: Player) = gameConfigMap[player]
}