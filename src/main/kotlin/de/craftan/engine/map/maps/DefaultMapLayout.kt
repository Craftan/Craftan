package de.craftan.engine.map.maps

import de.craftan.bridge.map.CraftanMapLayout
import de.craftan.engine.map.*

class DefaultMapLayout(
    override val name: String = "Default",
) : CraftanMapLayout {
    override val rows: List<LayoutRow<GameTile>> =
        listOf(
            layoutRow(
                GameTile(MaterialType.WHEAT, DiceNumber.FOUR),
                GameTile(MaterialType.WOOD, DiceNumber.SIX),
                GameTile(MaterialType.WHEAT, DiceNumber.NINE),
            ),
            layoutRow(
                GameTile(MaterialType.BRICK, DiceNumber.TWO),
                GameTile(MaterialType.WOOD, DiceNumber.FIVE),
                GameTile(MaterialType.SHEEP, DiceNumber.TWELVE),
                GameTile(MaterialType.SHEEP, DiceNumber.FOUR),
            ),
            layoutRow(
                GameTile(MaterialType.SHEEP, DiceNumber.NINE),
                GameTile(MaterialType.BRICK, DiceNumber.EIGHT),
                GameTile(MaterialType.DESERT, DiceNumber.SEVEN),
                GameTile(MaterialType.STONE, DiceNumber.EIGHT),
                GameTile(MaterialType.SHEEP, DiceNumber.TEN),
            ),
            layoutRow(
                GameTile(MaterialType.WOOD, DiceNumber.THREE),
                GameTile(MaterialType.STONE, DiceNumber.FIVE),
                GameTile(MaterialType.BRICK, DiceNumber.TEN),
                GameTile(MaterialType.WOOD, DiceNumber.ELEVEN),
            ),
            layoutRow(
                GameTile(MaterialType.WHEAT, DiceNumber.NINE),
                GameTile(MaterialType.WHEAT, DiceNumber.SIX),
                GameTile(MaterialType.STONE, DiceNumber.ELEVEN),
            ),
        )
    override val includesWater: Boolean = false
}
