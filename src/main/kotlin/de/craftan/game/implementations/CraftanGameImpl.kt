package de.craftan.game.implementations

import de.craftan.game.*
import de.craftan.game.events.GameStartEvent

class CraftanGameImpl(
    override val players: List<CraftanPlayer>,
    override val config: CraftanGameConfig,
) : CraftanGame() {
    override var round: CraftanGameRound = PreGameRound(players[0])

    override val playerSequence: Sequence<CraftanPlayer> = TODO()

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
