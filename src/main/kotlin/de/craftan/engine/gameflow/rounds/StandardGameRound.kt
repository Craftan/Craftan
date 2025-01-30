package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.GlobalGameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.flows.InitRoundFlow
import de.craftan.engine.gameflow.flows.StandardRoundFlow

class StandardGameRound (
    override val game: CraftanGame,
    override val name: String = "Init game",
): GlobalGameRound() {
    override val index: Int = game.roundIndex
    override val flow: RoundFlow = StandardRoundFlow(game, this)
}