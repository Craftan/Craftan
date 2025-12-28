package de.craftan.engine.implementations

import de.craftan.bridge.CraftanBridgePlayer
import de.craftan.bridge.CraftanPlayerInventory
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import org.bukkit.Location
import org.bukkit.entity.Player
import java.awt.Color

class CraftanBridgePlayerImpl(
    override val bukkitPlayer: Player,
    override val game: CraftanGame? = null,
    override val inventory: CraftanPlayerInventory = CraftanPlayerInventory(),
    override val victoryPoints: Int = 0,
    override val teamColor: Color = Color.RED,
): CraftanBridgePlayer {



}