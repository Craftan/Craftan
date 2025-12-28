package de.craftan.bridge.map

import com.fastasyncworldedit.core.FaweAPI
import com.sk89q.worldedit.math.BlockVector3
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.engine.map.CraftanMapLayout
import de.craftan.engine.map.GameTile
import de.craftan.engine.map.TileCoordinate
import de.craftan.structures.hexagonStructure
import de.craftan.structures.placeStructure
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

/**
 * Models the actual ingame map of a craftan game and connects it to the engine.
 *
 * The tiles work via the QRS coordinates. Based on those we calculate the ingame Position of the tiles
 * @see GameTile
 * @see CraftanMapLayout
* @see de.craftan.engine.CraftanGame
*/
class CraftanBridgeMap(
    val lobby: CraftanLobby,
) {
    /**
     * center coordinate of the created map
     */
    val center: BlockVector3 = lobby.board.center

    /**
     * bukkitWorld the map is in
     */
    val bukkitWorld: World = Bukkit.getWorld(lobby.board.worldEditWorld.name)!!
    private val worldEditWorld: com.sk89q.worldedit.world.World = FaweAPI.getWorld(bukkitWorld.name)!!
    private val hexagonSize: HexagonSize = HexagonSize()
    private val spacing = lobby.board.spacing
    val hitBoxes = mutableListOf<CraftanHitbox>()

    /**
     * Builds the map
     */
    fun build() {
        for (tile in lobby.board.layout.map.tiles.values) {
            val position = getPositionFromCoordinates(tile.coordinate)
            buildTile(tile, position)
        }
    }

    /**
     * Converts the QRS coordinates to an ingame Position.
     * No clue how it works, so I won't bother explaining
     * Change on your own risk.
     */
    private fun getPositionFromCoordinates(coordinate: TileCoordinate): BlockVector3 =
        BlockVector3.at(
            center.x() - coordinate.r * (hexagonSize.xSize + spacing - hexagonSize.xOffset).toDouble(),
            center.y().toDouble(),
            center.z() + (coordinate.q) * (hexagonSize.zSize + spacing) + coordinate.r.toDouble() / 2 * (hexagonSize.zSize + spacing) + 1,
        )

    private fun buildTile(
        tile: GameTile,
        position: BlockVector3,
    ) {
        val mapTile = MapTile(hexagonSize, position, bukkitWorld, tile)

        placeTile(mapTile, worldEditWorld)
        generateHitBoxes(mapTile, spacing)
    }

    private fun placeTile(
        tile: MapTile,
        world: com.sk89q.worldedit.world.World,
    ) {
        val position = tile.center
        placeStructure(position, world, hexagonStructure)

        val bukkitWorld = Bukkit.getWorld(world.name) ?: return
        val location = Location(bukkitWorld, position.x().toDouble(), position.y() + 2.0, position.z().toDouble())
        val textDisplay = bukkitWorld.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay

        textDisplay.text(
            Component.text(
                "${tile.gameTile.tileInfo.type} - ${tile.gameTile.tileInfo.type}" +
                    " R: ${tile.gameTile.coordinate.r};" +
                    " S: ${tile.gameTile.coordinate.s};" +
                    " Q: ${tile.gameTile.coordinate.q}",
            ),
        )
        textDisplay.transformation = Transformation(Vector3f(), AxisAngle4f(), Vector3f(3F, 3F, 3F), AxisAngle4f())
        textDisplay.billboard = Display.Billboard.CENTER
    }

    private fun generateHitBoxes(
        tile: MapTile,
        spacing: Int,
    ) {
        val width = tile.size.zSize
        val z0Offset = (width / 2 + 1) + (spacing / 2)

        val leftLocation = tile.center.subtract(0, 0, z0Offset)
        val rightLocation = tile.center.add(0, 0, z0Offset)

        val locations = listOf(leftLocation, rightLocation)

        locations.forEach { spawnHitBox(Location(tile.world, it.x().toDouble() + 0.5, it.y().toDouble(), it.z().toDouble() + 0.5)) }
    }

    private fun spawnHitBox(location: Location) {
        hitBoxes += CraftanHitbox(lobby, location)
    }
}

/**
 * A MapTile adds the minecraft specific infos to the GameTile.
 * Adds the popsition of the tile in Minecraft, through its size, center and world
 */
data class MapTile(
    val size: HexagonSize,
    val center: BlockVector3,
    val world: World,
    val gameTile: GameTile,
)

data class HexagonSize(
    /**
     * Height of the tile
     */
    val xSize: Int = 41,
    /**
     * Width of the tile
     */
    val zSize: Int = 35,
    /**
     * Offset to shift down tiles in rows
     */
    val xOffset: Int = 11,
)
