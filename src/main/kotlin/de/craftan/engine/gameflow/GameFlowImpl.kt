package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.rounds.GameRound
import de.craftan.engine.gameflow.rounds.InitGameRound
import de.craftan.engine.gameflow.rounds.NormalGameRound
import net.ormr.eventbus.EventBus

class GameFlowImpl(
    override val players: List<CraftanPlayer>,
) : GameFlow(){
    override fun nextRound() {
        println("Next round")
        round = NormalGameRound(players, this)
    }

    override fun init(eventBus: EventBus<Any, CraftanGameEvent>) {
        this.eventBus = eventBus
        round = InitGameRound(players, this)
    }
}