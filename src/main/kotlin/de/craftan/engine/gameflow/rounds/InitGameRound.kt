package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.flows.InitTurnFlow
import de.craftan.engine.gameflow.flows.TurnFlow
import net.ormr.eventbus.EventBus

/**
 * The round in the setup stage of the game,
 * where the players place their first settlements and roads,
 */
class InitGameRound(
    eventBus: EventBus<Any, CraftanGameEvent>,
    val players: List<CraftanPlayer>,
) : GameRound(eventBus) {
    override val turnSequence: MutableList<Pair<CraftanPlayer, TurnFlow>> = mutableListOf()
    override val name: String = "Init game"

    init {
        // Add the init states individually
        // Place the first settlement
        players.forEach {
            turnSequence.add(Pair(it, InitTurnFlow(eventBus, this)))
        }
        // Place the second settlement
        players.reversed().forEach {
            turnSequence.add(Pair(it, InitTurnFlow(eventBus, this)))
        }
    }
}
