package de.craftan.engine.gameflow.events.actions

import de.craftan.engine.CraftanEvent
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.Direction
import de.craftan.engine.structures.CraftanStructure

data class PlacedRobberEvent (
    override val game: CraftanGame,
    val player: CraftanPlayer,
    val coordinates: TileCoordinate,
) : CraftanEvent(game)