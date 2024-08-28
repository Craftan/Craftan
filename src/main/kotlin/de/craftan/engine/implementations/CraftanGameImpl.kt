package de.craftan.engine.implementations

import de.craftan.engine.*
import de.craftan.engine.events.GameStartEvent
import de.craftan.engine.flows.PreGameFlow

class CraftanGameImpl(
    override val players: List<CraftanPlayer>,
    override val config: CraftanGameConfig,
) : CraftanGame() {
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
    }

    override fun start() {
    }
}
