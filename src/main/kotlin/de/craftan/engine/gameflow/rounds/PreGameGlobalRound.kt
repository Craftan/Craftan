package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.GlobalGameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.flows.PreRoundFlow

/**
 * A round before the actual game, where for example the turn sequence gets determined
 */
class PreGameGlobalRound(
    override val game: CraftanGame,
    override val name: String = "Pre game",
) : GlobalGameRound {
    override val index: Int = -1
    override val flow: RoundFlow = PreRoundFlow(game)
}
