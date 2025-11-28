package de.craftan.engine.gameflow.rounds

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.flows.TurnFlow

/**
 * The round in the setup stage of the game,
 * where the players place their first settlements and roads,
 */
class InitGameRound(
    override val game: CraftanGame,
    override val name: String = "Init game",
    override val roundNumber: Int = 0,
    override var turnIndex: Int = 0,
    override val turnSequence: List<Pair<CraftanPlayer, TurnFlow>>,
) : GameRound
