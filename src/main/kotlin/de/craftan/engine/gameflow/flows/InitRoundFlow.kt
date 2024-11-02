package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.states.AwaitStructurePlacement

/**
 * Models how a round flow should look like in the setup stage of the game,
 * where the players place their first settlements and roads
 */
class InitRoundFlow(
    game: CraftanGame,
) : RoundFlow(AwaitStructurePlacement(game), game) {
    init {
        addState(AwaitStructurePlacement(game))
    }
}
