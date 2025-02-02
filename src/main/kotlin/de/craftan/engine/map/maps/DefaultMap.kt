package de.craftan.engine.map.maps

import de.craftan.engine.CraftanResource
import de.craftan.engine.map.*
import de.craftan.engine.map.CraftanMap
import de.craftan.engine.resources.WoodResource

class DefaultMap(
    override val name: String = "Default",
) : CraftanMap {
    override val coordinatesToTile: MutableMap<TileCoordinate, GameTile> =
        toCoordinateToGameTileMap(
            listOf(
                listOf(
                    TileInfo(WoodResource, DiceNumber.FOUR),
                    TileInfo(WoodResource, DiceNumber.SIX),
                    TileInfo(WoodResource, DiceNumber.NINE),
                    TileInfo(WoodResource, DiceNumber.TWELVE),
                    TileInfo(WoodResource, DiceNumber.EIGHT),
                ),
                listOf(
                    TileInfo(WoodResource, DiceNumber.TWO),
                    TileInfo(WoodResource, DiceNumber.FIVE),
                    TileInfo(WoodResource, DiceNumber.TWELVE),
                    TileInfo(WoodResource, DiceNumber.FOUR),
                ),
                listOf(
                    TileInfo(WoodResource, DiceNumber.NINE),
                    TileInfo(WoodResource, DiceNumber.EIGHT),
                    TileInfo(WoodResource, DiceNumber.SEVEN),
                    TileInfo(WoodResource, DiceNumber.EIGHT),
                    TileInfo(WoodResource, DiceNumber.TEN),
                ),
                listOf(
                    TileInfo(WoodResource, DiceNumber.THREE),
                    TileInfo(WoodResource, DiceNumber.FIVE),
                    TileInfo(WoodResource, DiceNumber.TEN),
                    TileInfo(WoodResource, DiceNumber.ELEVEN),
                ),
                listOf(
                    TileInfo(WoodResource, DiceNumber.NINE),
                    TileInfo(WoodResource, DiceNumber.SIX),
                    TileInfo(WoodResource, DiceNumber.ELEVEN),
                ),
            ),
        )
}
