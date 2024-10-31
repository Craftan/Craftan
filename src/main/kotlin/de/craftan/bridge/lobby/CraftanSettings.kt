package de.craftan.bridge.lobby

import de.craftan.engine.CraftanGameExtension

val craftanDefaultSettings = MutableCraftanSettings(mutableListOf(), 12, 7)

/**
 * Models the settings for a game
 * @param mapLayout the map to use
 * @param extensions list of extensions to influence the game
 * @param pointsToWin the minimum points a player needs to fin
 * @param cardsLimit the max cards a player can have before disposing when the robber comes
 */
data class CraftanSettings(
    val extensions: List<CraftanGameExtension>,
    val pointsToWin: Int,
    val cardsLimit: Int,
)

data class MutableCraftanSettings(
    val extensions: MutableList<CraftanGameExtension>,
    var pointsToWin: Int,
    var cardsLimit: Int,
) {
    fun toCraftanSettings(): CraftanSettings = CraftanSettings(extensions.toList(), pointsToWin, cardsLimit)
}
