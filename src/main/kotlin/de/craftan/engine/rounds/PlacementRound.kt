package de.craftan.engine.rounds

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.GameRound
import de.craftan.engine.RoundFlow

class PlacementRound(
    override val index: Int = -1,
    override val player: CraftanPlayer,
    override val flow: RoundFlow,
) : GameRound
