package de.craftan.bridge.lobby

import de.craftan.engine.CraftanGameExtension
import de.craftan.engine.map.CraftanMapLayout

/**
 * Models the settings for a game
 * @param mapLayout the map to use
 * @param extensions list of extensions to influence the game
 * @param pointsToWin the minimum points a player needs to fin
 * @param cardsLimit the max cards a player can have before disposing when the robber comes
 */
data class CraftanSettings(
    val mapLayout: CraftanMapLayout,
    val extensions: List<CraftanGameExtension>,
    val pointsToWin: Int,
    val cardsLimit: Int,
)

data class MutableCraftanSettings(
    var mapLayout: CraftanMapLayout,
    val extensions: MutableList<CraftanGameExtension>,
    var pointsToWin: Int,
    var cardsLimit: Int,
) {
    fun toCraftanSettings(): CraftanSettings = CraftanSettings(mapLayout, extensions.toList(), pointsToWin, cardsLimit)
}
