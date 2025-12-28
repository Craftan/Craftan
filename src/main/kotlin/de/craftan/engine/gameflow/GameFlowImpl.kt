package de.craftan.engine.gameflow

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.rounds.InitGameRound

class GameFlowImpl(
    override val players: List<CraftanPlayer>
) : GameFlow(){
    override fun nextRound() {
        TODO("Not yet implemented")
    }

    override fun init() {
        round = InitGameRound(players)
    }
}