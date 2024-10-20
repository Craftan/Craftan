package de.craftan.engine.implementations

import de.craftan.engine.*
import de.craftan.engine.events.GameStartEvent
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.flows.PreGameFlow
import de.craftan.engine.gameflow.rounds.PreGameRound

class CraftanGameImpl(
    override val players: List<CraftanPlayer>,
    override val config: CraftanGameConfig,
) : CraftanGame() {
    var hasFinishedPreGame = false
    var hasFinishedPlacing = false

    override var round: GameRound = PreGameRound(players[0], PreGameFlow(this))

    override var state: CraftanGameState = CraftanGameState.WAITING

    override fun startGame() {
        if (state != CraftanGameState.WAITING) {
            error("Game must be waiting to be started")
        }

        eventBus.fire(GameStartEvent(this))

        state = CraftanGameState.PRE_GAME
    }

    override fun nextRound() {
        if (round is PreGameRound) {
            hasFinishedPreGame = true
        }

        if (hasFinishedPlacing && !hasFinishedPreGame) {
        }
    }

    override fun start() {
    }
}
