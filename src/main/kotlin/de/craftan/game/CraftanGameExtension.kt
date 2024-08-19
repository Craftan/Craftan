package de.craftan.game

/**
 * Models an extension to the base craftan game
 */
interface CraftanGameExtension {
    /**
     * The current game, this extension is adding on top of
     */
    val game: CraftanGame
}
