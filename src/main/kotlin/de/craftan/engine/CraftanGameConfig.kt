package de.craftan.engine

/**
 * Models a static game configurations of a craftan game
 * @see CraftanGame
 */
data class CraftanGameConfig(
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
    val extensions: MutableList<CraftanGameExtension> = mutableListOf(),
    var pointsToWin: Int = 12,
    var timeToRollDice: Int = 60,
    var timeToFinishTurn: Int = 60,
    var cardsLimit: Int = 7,
) {
    fun toCraftanGameConfig(): CraftanGameConfig = CraftanGameConfig(extensions.toList(), pointsToWin, timeToRollDice, timeToFinishTurn, cardsLimit)
}
