package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.flows.TurnFlow
import de.craftan.engine.gameflow.rounds.GameRound
import net.ormr.eventbus.EventBus

abstract class GameFlow {

    /**
     * The current round index
     * 0 => Before first round
     * 1 => First round
     * 2 => Second round
     * etc.
     */
    var roundIndex = 0

    var eventBus: EventBus<Any, CraftanGameEvent> = EventBus()

    /**
     * The current round of the game
     */
    lateinit var round: GameRound

    /**
     * All participating players
     */
    abstract val players: List<CraftanPlayer>

    /**
     * Starts the next round
     */
    abstract fun nextRound()

    /**
     * Init the GameFlow
     */
    abstract fun init(eventBus: EventBus<Any, CraftanGameEvent>)

    fun turnFlow(): TurnFlow = round?.turnSequence[round!!.turnIndex]?.second!!
}