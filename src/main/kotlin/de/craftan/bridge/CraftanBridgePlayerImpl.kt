package de.craftan.bridge

import de.craftan.engine.CraftanGame
import org.bukkit.entity.Player
import java.awt.Color

class CraftanBridgePlayerImpl(
    override var bukkitPlayer: Player,
    override val game: CraftanGame? = null,
    override val inventory: CraftanPlayerInventory = CraftanPlayerInventory(),
    override val victoryPoints: Int = 0,
    override val teamColor: Color = Color.RED,
    var left: Boolean = false
): CraftanBridgePlayer