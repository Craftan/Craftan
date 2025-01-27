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
}
