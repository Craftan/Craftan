package de.craftan.engine.map.maps

import de.craftan.engine.map.*
import de.craftan.engine.map.CraftanMapLayout

class DefaultMapLayout(
    override val name: String = "Default",
) : CraftanMapLayout {
    override val coordinatesToTile: MutableMap<TileCoordinate, GameTile> =
        toCoordinateToGameTileMap(
            listOf(
                listOf(
                    TileInfo(MaterialType.WHEAT, DiceNumber.FOUR),
                    TileInfo(MaterialType.WOOD, DiceNumber.SIX),
                    TileInfo(MaterialType.WHEAT, DiceNumber.NINE),
                    TileInfo(MaterialType.SHEEP, DiceNumber.TWELVE),
                    TileInfo(MaterialType.STONE, DiceNumber.EIGHT),
                ),
                listOf(
                    TileInfo(MaterialType.BRICK, DiceNumber.TWO),
                    TileInfo(MaterialType.WOOD, DiceNumber.FIVE),
                    TileInfo(MaterialType.SHEEP, DiceNumber.TWELVE),
                    TileInfo(MaterialType.SHEEP, DiceNumber.FOUR),
                ),
                listOf(
                    TileInfo(MaterialType.SHEEP, DiceNumber.NINE),
                    TileInfo(MaterialType.BRICK, DiceNumber.EIGHT),
                    TileInfo(MaterialType.DESERT, DiceNumber.SEVEN),
                    TileInfo(MaterialType.STONE, DiceNumber.EIGHT),
                    TileInfo(MaterialType.SHEEP, DiceNumber.TEN),
                ),
                listOf(
                    TileInfo(MaterialType.WOOD, DiceNumber.THREE),
                    TileInfo(MaterialType.STONE, DiceNumber.FIVE),
                    TileInfo(MaterialType.BRICK, DiceNumber.TEN),
                    TileInfo(MaterialType.WOOD, DiceNumber.ELEVEN),
                ),
                listOf(
                    TileInfo(MaterialType.WHEAT, DiceNumber.NINE),
                    TileInfo(MaterialType.WHEAT, DiceNumber.SIX),
                    TileInfo(MaterialType.STONE, DiceNumber.ELEVEN),
                ),
            ),
        )
}
