package de.craftan.game.lobby

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.World

data class CraftanLobby(
    val world: World,
    val center: BlockVector3,
    val spacing: Int,
)
