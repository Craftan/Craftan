package de.craftan.engine

import de.craftan.engine.gameflow.GameFlow
import de.craftan.engine.gameflow.action.CraftanGameActionEvent
import de.craftan.engine.resources.CraftanResourceType

class CraftanGameImpl(override val config: CraftanGameConfig, override val gameFlow: GameFlow): CraftanGame(config) {
    override val stateHandler: CraftanGameStateHandler = CraftanGameStateHandler(
        config.craftanMapLayout.map,
        gameFlow.players.associateWith { config.resources.associateWith { 0 } as MutableMap<CraftanResourceType, Int> },
        gameFlow.players.associateWith { config.cards.associateWith { 0 } },
        gameFlow.players.associateWith { 0 }
    )

    override fun start() {
        registerListener()
        gameFlow.init(eventBus)
    }

    fun registerListener() {
        eventBus.on<CraftanGameActionEvent<*,*,*>> {
            val result = verifyAndInvoke()
            gameFlow.round.currentTurn().second.nextState(this, result)
        }
        eventBus.on<CraftanGameEndTurnEvent> {
            gameFlow.round.nextTurn()
        }
    }
}