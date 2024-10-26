package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.PlayerGameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.flows.InitRoundFlow

class InitGameRound(
    override val game: CraftanGame,
    override val name: String = "Init game",
    override val player: CraftanPlayer,
) : PlayerGameRound {
    override val index: Int = -1
    override val flow: RoundFlow = InitRoundFlow(game)
}
