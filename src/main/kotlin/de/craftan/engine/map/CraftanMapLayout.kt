package de.craftan.engine.map

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.World
import de.craftan.engine.lobby.CraftanLobby
import de.craftan.structures.hexagonStructure
import de.craftan.structures.placeStructure
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

interface CraftanMapLayout {
    val rows: List<LayoutRow<GameTile>>
    val includesWater: Boolean

    /**
     * Builds the map from [rows]
     *
     * The maps layout is always as follows:
     *
     * ^x (minecraft)
     * |
     * |----> z (minecraft)
     *
     * so in the x coordinate is actually up in the map,
     * and y is left and right.
     *
     * @param lobby where to build the map
     */
    fun build(lobby: CraftanLobby) {
        val center = lobby.center

        val xWidth = 41
        val zWidth = 35
        val hexagonSize = HexagonSize(xWidth, zWidth, 11)
        val spacing = lobby.spacing

        var location = center
        val centerRowIndex = rows.size / 2 + 1

        for ((index, row) in rows.withIndex()) {
            buildRow(location, row.tiles, hexagonSize, lobby)

            val isAboveCenter = index + 2 > centerRowIndex

            location =
                location.add(
                    (hexagonSize.xSize + spacing - hexagonSize.xOffset),
                    0,
                    (hexagonSize.zSize / 2 + 1 + spacing / 2) * if (isAboveCenter) 1 else -1,
                )
        }
    }

    private fun buildRow(
        startPoint: BlockVector3,
        tiles: List<GameTile>,
        size: HexagonSize,
        lobby: CraftanLobby,
    ) {
        var center = startPoint
        for (tile in tiles) {
            placeTile(tile, center, lobby.world)
            center = center.add(0, 0, size.zSize + lobby.spacing)
        }
    }

    private fun placeTile(
        tile: GameTile,
        position: BlockVector3,
        world: World,
    ) {
        placeStructure(position, world, hexagonStructure)

        val bukkitWorld = Bukkit.getWorld(world.name) ?: return
        val location = Location(bukkitWorld, position.x().toDouble(), position.y() + 2.0, position.z().toDouble())
        val textDisplay = bukkitWorld.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay
        textDisplay.text(Component.text("${tile.type} - ${tile.chance}"))
        textDisplay.transformation = Transformation(Vector3f(), AxisAngle4f(), Vector3f(10F, 10F, 10F), AxisAngle4f())
        textDisplay.billboard = Display.Billboard.CENTER
    }
}

data class HexagonSize(
    val xSize: Int,
    val zSize: Int,
    val xOffset: Int,
)
