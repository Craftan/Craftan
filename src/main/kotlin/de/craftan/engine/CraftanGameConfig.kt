package de.craftan.engine

import de.craftan.engine.map.CraftanMapLayout
import de.craftan.engine.map.maps.DefaultMapLayout
import de.craftan.engine.resources.CraftanActionCard
import de.craftan.engine.resources.CraftanActionCardType
import de.craftan.engine.resources.CraftanResourceType

/**
 * Models a static game configurations of a craftan game
 * @see CraftanGame
 */
data class CraftanGameConfig(
    /**
     * The Map Layout to be used
     */
    val craftanMapLayout: CraftanMapLayout,

    /**
     * The resources this game is played with
     */
    val resources: List<CraftanResourceType>,

    /**
     * The cards this game is played with
     */
    val cards: List<CraftanActionCard>,

    /**
     * A list of all extensions
     */
    val extensions: List<CraftanGameExtension>,

    /**
     * Victory points a player needs, to win a game
     */
    val pointsToWin: Int,

    /**
     * Time the player has to roll his dice, before the system randomly rolls the dice
     */
    val timeToRollDice: Int,

    val timeToFinishTurn: Int,

    /**
     * Limit of cards
     * When exceeded, players have to discard 50% of their cards
     */
    val cardsLimit: Int
)

data class MutableCraftanGameConfig(
    val mapLayout: CraftanMapLayout = DefaultMapLayout(),
    val resources: MutableList<CraftanResourceType> = CraftanResourceType.entries.toMutableList(),
    val cards: MutableList<CraftanActionCard> = CraftanActionCardType.entries.toMutableList(),
    val extensions: MutableList<CraftanGameExtension> = mutableListOf(),
    var pointsToWin: Int = 12,
    var timeToRollDice: Int = 60,
    var timeToFinishTurn: Int = 60,
    var cardsLimit: Int = 7,
) {
    fun toCraftanGameConfig(): CraftanGameConfig = CraftanGameConfig(mapLayout, resources, cards, extensions.toList(), pointsToWin, timeToRollDice, timeToFinishTurn, cardsLimit)
}
