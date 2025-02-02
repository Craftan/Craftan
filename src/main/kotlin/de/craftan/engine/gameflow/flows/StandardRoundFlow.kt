package de.craftan.engine.gameflow.flows

import de.craftan.engine.CraftanGame
import de.craftan.engine.gameflow.GameRound
import de.craftan.engine.gameflow.RoundFlow
import de.craftan.engine.gameflow.events.actions.PlacedRobberEvent
import de.craftan.engine.gameflow.events.actions.PlacedStructureEvent
import de.craftan.engine.gameflow.events.actions.RolledDiceEvent
import de.craftan.engine.gameflow.states.AwaitStructurePlacementRoundState
import de.craftan.engine.gameflow.states.AwaitingDiceRoundState
import de.craftan.engine.gameflow.states.RobberRoundState
import de.craftan.engine.gameflow.states.StandardRoundState
import de.craftan.engine.map.DiceNumber
import de.craftan.engine.structures.City
import de.craftan.engine.structures.Settlement

class StandardRoundFlow(
    game: CraftanGame,
    round: GameRound
) : RoundFlow(game, round) {
    init {
        addState(AwaitingDiceRoundState(game, game.playerSequence.currentPlayer))
        states.last().eventBus.on<RolledDiceEvent> {
            if (result == DiceNumber.SEVEN) {
                val robberRoundState = RobberRoundState(game)
                states.add(1,robberRoundState)
                robberRoundState.eventBus.on<PlacedRobberEvent> {
                    TODO()
                }
            } else {
                game.map.coordinatesToTile.values.forEach lit@{ tile ->
                    if (tile.tileInfo.chance != result) return@lit
                    tile.nodes.values.forEach { node ->
                        if (node.structureInfo.structure == null) return@lit
                        if (node.structureInfo.structure is Settlement) player.inventory.add(tile.tileInfo.type, 1)
                        if (node.structureInfo.structure is City) player.inventory.add(tile.tileInfo.type, 2)
                    }
                }
            }
            nextState()
        }

        addState(StandardRoundState(game, game.playerSequence.currentPlayer))
        states.last().eventBus.on<PlacedStructureEvent> {
            TODO()
            nextState()
        }
    }
}