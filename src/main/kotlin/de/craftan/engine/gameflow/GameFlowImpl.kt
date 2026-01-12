package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.rounds.InitGameRound
import net.ormr.eventbus.EventBus

class GameFlowImpl(
    override val players: List<CraftanPlayer>,
) : GameFlow(){
    override fun nextRound() {
        TODO("Not yet implemented")
    }

    override fun init(eventBus: EventBus<Any, CraftanGameEvent>) {
        this.eventBus = eventBus
        round = InitGameRound(this.eventBus, players)
    }
}