package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.RoundFlow

class PlacementRound(
    override val index: Int = -1,
    override val player: CraftanPlayer,
    override val flow: RoundFlow,
) : GameRound
