package de.craftan.engine.map.maps

import de.craftan.engine.map.*
import de.craftan.engine.map.CraftanMapLayout

class DefaultMapLayout(
    override val name: String = "Default",
) : CraftanMapLayout {
    override val map: CraftanMap = CraftanMap(
        toCoordinateToGameTileMap(
            listOf(
                listOf(
                    TileInfo(MapMaterialType.WHEAT, DiceNumber.FOUR),
                    TileInfo(MapMaterialType.WOOD, DiceNumber.SIX),
                    TileInfo(MapMaterialType.WHEAT, DiceNumber.NINE),
                    TileInfo(MapMaterialType.SHEEP, DiceNumber.TWELVE),
                    TileInfo(MapMaterialType.STONE, DiceNumber.EIGHT),
                ),
                listOf(
                    TileInfo(MapMaterialType.BRICK, DiceNumber.TWO),
                    TileInfo(MapMaterialType.WOOD, DiceNumber.FIVE),
                    TileInfo(MapMaterialType.SHEEP, DiceNumber.TWELVE),
                    TileInfo(MapMaterialType.SHEEP, DiceNumber.FOUR),
                ),
                listOf(
                    TileInfo(MapMaterialType.SHEEP, DiceNumber.NINE),
                    TileInfo(MapMaterialType.BRICK, DiceNumber.EIGHT),
                    TileInfo(MapMaterialType.DESERT, DiceNumber.SEVEN),
                    TileInfo(MapMaterialType.STONE, DiceNumber.EIGHT),
                    TileInfo(MapMaterialType.SHEEP, DiceNumber.TEN),
                ),
                listOf(
                    TileInfo(MapMaterialType.WOOD, DiceNumber.THREE),
                    TileInfo(MapMaterialType.STONE, DiceNumber.FIVE),
                    TileInfo(MapMaterialType.BRICK, DiceNumber.TEN),
                    TileInfo(MapMaterialType.WOOD, DiceNumber.ELEVEN),
                ),
                listOf(
                    TileInfo(MapMaterialType.WHEAT, DiceNumber.NINE),
                    TileInfo(MapMaterialType.WHEAT, DiceNumber.SIX),
                    TileInfo(MapMaterialType.STONE, DiceNumber.ELEVEN),
                ),
            ),
        )
    )
}
