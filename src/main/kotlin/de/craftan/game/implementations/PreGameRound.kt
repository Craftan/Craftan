package de.craftan.game.implementations

import de.craftan.game.CraftanGameRound
import de.craftan.game.CraftanPlayer
import de.craftan.game.CraftanRoundState

class PreGameRound(
    override val player: CraftanPlayer,
) : CraftanGameRound {
    var diceResult: Int = 0
    override val index: Int = -1

    override val state: CraftanRoundState
        get() = TODO("Not yet implemented")
}
