package de.craftan.game

/**
 * Models a static game configurations of a craftan game
 * @see CraftanGame
 */
interface CraftanGameConfig {
    /**
     * A list of all extensions
     */
    val extensions: List<CraftanGameExtension>

    val availableResources: List<CraftanResource>

    /**
     * Victory points a player needs, to win a game
     */
    val pointsToWin: Int

    /**
     * Limit of cards
     * When exceeded, players have to discard 50% of their cards
     */
    val cardsLimit: Int
}
