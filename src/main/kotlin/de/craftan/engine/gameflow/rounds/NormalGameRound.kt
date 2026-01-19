package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameFlow
import de.craftan.engine.gameflow.rounds.GameRound
import de.craftan.engine.gameflow.flows.InitTurnFlow
import de.craftan.engine.gameflow.flows.NormalTurnFlow
import de.craftan.engine.gameflow.flows.TurnFlow
import kotlin.collections.forEach

class NormalGameRound(
    val players: List<CraftanPlayer>,
    flow: GameFlow,
) : GameRound(flow) {
    override val turnSequence: MutableList<Pair<CraftanPlayer, TurnFlow>> = mutableListOf()
    override val name: String = "Normal round"

    init {
        println("NormalGameRound: Players: " + players.joinToString(separator = ", ") { it.name })
        players.forEach {
            turnSequence.add(Pair(it, NormalTurnFlow( this)))
        }
    }
}
