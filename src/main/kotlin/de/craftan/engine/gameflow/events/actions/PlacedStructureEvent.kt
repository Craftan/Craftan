package de.craftan.engine.gameflow.events.actions

import de.craftan.engine.CancelableCraftanEvent
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.Direction
import de.craftan.engine.structures.CraftanStructure

data class PlacedStructureEvent(
    override val game: CraftanGame,
    val player: CraftanPlayer,
    val coordinates: TileCoordinate,
    val direction: Direction,
    val structureInfo: CraftanStructure,
) : CancelableCraftanEvent(game)
