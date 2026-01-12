package de.craftan.engine.gameflow.action

import de.craftan.engine.CraftanGameStateHandler
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameFlow
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.CraftanDirection
import de.craftan.engine.structures.CraftanStructure

class PlaceStructureAction(
    val requireResources: Boolean = true,
): CraftanGameAction<PlaceStructureActionData> {

    override fun invoke(player: CraftanPlayer, data: PlaceStructureActionData, stateHandler: CraftanGameStateHandler): Boolean {
        stateHandler.placeStructure(player, data.structure, data.tileCoordinate, data.direction, requireResources)
        return true
    }

    override fun verify(player: CraftanPlayer, data: PlaceStructureActionData, stateHandler: CraftanGameStateHandler, gameFlow: GameFlow): Boolean {
        if (player != gameFlow.round!!.currentTurn().first)
        if (!stateHandler.map.isEmpty(data.tileCoordinate, data.direction)) return false
        if (requireResources && !stateHandler.hasResources(player, data.structure.cost)) return false
        // TODO Check max amount buildings
        // TODO Check distance between settlements

        return true
    }

}

class PlaceStructureActionData (
    val structure: CraftanStructure,
    val tileCoordinate: TileCoordinate,
    val direction: CraftanDirection,
): CraftanActionData



