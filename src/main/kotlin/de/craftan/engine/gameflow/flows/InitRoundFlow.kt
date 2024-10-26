package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.states.AwaitStructurePlacement

class InitRoundFlow(
    game: CraftanGame,
) : RoundFlow(AwaitStructurePlacement(game), game) {
    init {
        addState(AwaitStructurePlacement(game))
    }
}
