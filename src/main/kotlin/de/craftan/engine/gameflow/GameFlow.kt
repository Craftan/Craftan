package de.craftan.engine.gameflow

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.flows.TurnFlow
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

    // TODO Fix this terrible eventbus thing with like an eventbusmanager or sth so I dont have to pass the instance through all my objects
    var eventBus: EventBus<Any, CraftanGameEvent> = EventBus()

    /**
     * The current round of the game
     */
    // Todo Why the fuck is this nullable
    var round: GameRound? = null

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