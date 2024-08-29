package de.craftan.engine.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.RoundFlow
import de.craftan.engine.TurnSequence
import de.craftan.engine.events.actions.RolledDiceEvent
import de.craftan.engine.map.DiceNumber
import de.craftan.engine.states.AwaitingDiceState

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
        TODO("Missing in game flow")
    }
}
