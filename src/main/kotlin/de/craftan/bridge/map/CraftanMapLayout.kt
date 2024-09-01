package de.craftan.bridge.map

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.World
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.engine.map.GameTile
import de.craftan.engine.map.LayoutRow
import de.craftan.structures.hexagonStructure
import de.craftan.structures.placeStructure
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

/**
 * Models a layout of the minecraft ingame map, to the craftan layout
 */
interface CraftanMapLayout {
    /**
     * Abstract name of this map layout, for example: default no water
     */
    val name: String

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
    fun build(lobby: CraftanLobby): CraftanMap {
        val map = CraftanMap(this)

        val center = lobby.center

        val xWidth = 41
        val zWidth = 35
        val hexagonSize = HexagonSize(xWidth, zWidth, 11)
        val spacing = lobby.spacing

        var location = center
        val centerRowIndex = rows.size / 2 + 1
        val centerTileIndex = rows[centerRowIndex].tiles.size / 2 + 1

        for ((index, row) in rows.withIndex()) {
            val tiles = buildRow(location, index, row.tiles, hexagonSize, lobby, TileLocation(centerRowIndex, centerTileIndex))

            val isAboveCenter = index + 2 > centerRowIndex

            location =
                location.add(
                    (hexagonSize.xSize + spacing - hexagonSize.xOffset),
                    0,
                    (hexagonSize.zSize / 2 + 1 + spacing / 2) * if (isAboveCenter) 1 else -1,
                )
        }

        return map
    }

    private fun buildRow(
        startPoint: BlockVector3,
        rowIndex: Int,
        tiles: List<GameTile>,
        size: HexagonSize,
        lobby: CraftanLobby,
        centerTile: TileLocation,
    ): List<MapTile> {
        val createdTiles = mutableListOf<MapTile>()
        var center = startPoint

        for (tile in tiles) {
            val bukkitWorld = Bukkit.getWorld(lobby.world.name)!!
            val mapTile = MapTile(0, 0, 0, size, center, bukkitWorld, tile)

            placeTile(mapTile, lobby.world)
            generateHitBoxes(mapTile, lobby.spacing)

            center = center.add(0, 0, size.zSize + lobby.spacing)
            createdTiles += mapTile
        }

        return createdTiles
    }

    private fun placeTile(
        tile: MapTile,
        world: World,
    ) {
        val position = tile.center
        placeStructure(position, world, hexagonStructure)

        val bukkitWorld = Bukkit.getWorld(world.name) ?: return
        val location = Location(bukkitWorld, position.x().toDouble(), position.y() + 2.0, position.z().toDouble())
        val textDisplay = bukkitWorld.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay

        textDisplay.text(Component.text("${tile.gameTile.type} - ${tile.gameTile.type} I: ${tile.i}; J: ${tile.j}; K: ${tile.k}"))
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
        val world = location.world

        println("Spawning hitbox at $location")

        val hitBox = world.spawnEntity(location, EntityType.INTERACTION) as Interaction
        hitBox.setNoPhysics(true)
        hitBox.setGravity(false)

        hitBox.interactionWidth = 3.0f
        hitBox.interactionHeight = 1.0f
    }
}

private data class TileLocation(
    val rowIndex: Int,
    val index: Int,
)

data class HexagonSize(
    /**
     * Height of the tile
     */
    val xSize: Int,
    /**
     * Width of the tile
     */
    val zSize: Int,
    /**
     * Offset to shift down tiles in rows
     */
    val xOffset: Int,
)
