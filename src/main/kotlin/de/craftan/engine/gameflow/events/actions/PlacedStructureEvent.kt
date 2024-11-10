package de.craftan.engine.gameflow.events.actions

import de.craftan.engine.CraftanEvent
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.structures.CraftanStructure

data class PlacedStructureEvent(
    override val game: CraftanGame,
    val player: CraftanPlayer,
    // TODO: Missing structure coordinates
    val structureInfo: CraftanStructure,
) : CraftanEvent(game)
