package de.craftan.engine.implementations

import de.craftan.bridge.lobby.CraftanSettings
import de.craftan.engine.*
import de.craftan.engine.events.GameStartEvent
import de.craftan.engine.flows.PreGameFlow
import de.craftan.engine.rounds.PreGameRound

class CraftanGameImpl(
    override val players: List<CraftanPlayer>,
    override val config: CraftanGameConfig,
    override val settings: CraftanSettings,
) : CraftanGame(settings) {
    var hasFinishedPreGame = false
    var hasFinishedPlacing = false

    override var round: GameRound = PreGameRound(PreGameFlow(this), this)

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
