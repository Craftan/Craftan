package de.craftan.engine.rounds

import de.craftan.engine.CraftanGame
import de.craftan.engine.GlobalGameRound
import de.craftan.engine.RoundFlow

class PreGameRound(
    override val flow: RoundFlow,
    override val game: CraftanGame,
    override val name: String = "Pre game",
) : GlobalGameRound {
    override val index: Int = -1
}
