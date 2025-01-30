package de.craftan.engine.implementations

import de.craftan.bridge.lobby.CraftanSettings
import de.craftan.engine.*
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.events.GameStartEvent
import de.craftan.engine.gameflow.rounds.InitGameRound
import de.craftan.engine.gameflow.rounds.PreGameGlobalRound
import de.craftan.engine.map.CraftanMap

class CraftanGameImpl(
    override val players: List<CraftanPlayer>,
    override val config: CraftanGameConfig,
    override val settings: CraftanSettings,
    override val map: CraftanMap,
) : CraftanGame(settings) {
    var hasFinishedPreGame = false
    var hasFinishedPlacing = false

    override var round: GameRound = PreGameGlobalRound(this)

    override var state: CraftanGameState = CraftanGameState.WAITING

    override fun startGame() {
        if (state != CraftanGameState.WAITING) {
            error("Game must be waiting to be started")
        }

        eventBus.fire(GameStartEvent(this))

        state = CraftanGameState.PRE_GAME
    }

    override fun nextRound() {
        roundIndex++
        if (round is PreGameGlobalRound) {
            hasFinishedPreGame = true
            round = InitGameRound(this)
            return
        }

        if (round is InitGameRound) {
            hasFinishedPlacing = true
            round = TODO("NormalGameRound")
            return
        }
        round = TODO("NormalGameRound")
    }

    override fun start() {
    }
}
