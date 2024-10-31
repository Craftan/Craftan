package de.craftan.bridge.lobby

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.World
import de.craftan.engine.map.CraftanMapLayout

/**
 * Models an actually existing GameMap for a game of Craftan
 * @param worldEditWorld the world
 * @param center the center of the board
 * @param spacing the spacing between tiles on the board
 */
data class CraftanBoard(
    val worldEditWorld: World,
    val center: BlockVector3,
    val spacing: Int,
    val layout: CraftanMapLayout,
)
