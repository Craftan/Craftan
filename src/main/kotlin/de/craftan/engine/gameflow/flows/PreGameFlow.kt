package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.events.actions.RolledDiceEvent
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.TurnSequence
import de.craftan.engine.gameflow.states.AwaitingDiceState
import de.craftan.engine.map.DiceNumber

class PreGameFlow(
    game: CraftanGame,
) : RoundFlow(AwaitingDiceState(game), game) {
    init {
        for (i in 1 until game.players.size) {
            addState(AwaitingDiceState(game))
        }

        states.forEach { it.eventBus.on<RolledDiceEvent> { addPlayerResult(player, result) } }
    }

    private val playerResult = mutableMapOf<CraftanPlayer, DiceNumber>()

    private fun addPlayerResult(
        player: CraftanPlayer,
        result: DiceNumber,
    ) {
        playerResult[player] = result
    }

    override fun finishFlow() {
        game.playerSequence = TurnSequence(playerResult.keys.sortedBy { playerResult[it]?.value })
        game.nextRound()
    }
}
