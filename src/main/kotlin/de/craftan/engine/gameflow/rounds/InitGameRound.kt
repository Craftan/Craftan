package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GlobalGameRound
import de.craftan.engine.gameflow.PlayerGameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.flows.InitRoundFlow

/**
 * The round in the setup stage of the game,
 * where the players place their first settlements and roads
 */
class InitGameRound(
    override val game: CraftanGame,
    override val name: String = "Init game",
) : GlobalGameRound() {
    override val index: Int = 0
    override val flow: RoundFlow = InitRoundFlow(game, this)

    private val firstPlacementAllDone = false
    override fun finishFlow() {
        if  (numberOfRepetitions < game.playerSequence.getPlayerAmount()) {
            numberOfRepetitions++
            if (!firstPlacementAllDone) {
                game.playerSequence.nextPlayer()
            } else game.playerSequence.previousPlayer()
            flow.repeat()
        } else {
            if (firstPlacementAllDone) {
                game.nextRound()
            } else {
                numberOfRepetitions = 0
                flow.repeat()
            }
        }
    }
}
