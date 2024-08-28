package de.craftan.engine.implementations

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.GameRound
import de.craftan.engine.RoundFlow

class PreGameRound(
    override val player: CraftanPlayer,
    override val flow: RoundFlow,
) : GameRound {
    var diceResult: Int = 0
    override val index: Int = -1
}
