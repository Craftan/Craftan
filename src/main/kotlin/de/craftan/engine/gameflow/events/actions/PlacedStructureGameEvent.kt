package de.craftan.engine.gameflow.events.actions

import de.craftan.engine.CraftanGameEvent
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.structures.CraftanStructure

data class PlacedStructureGameEvent(
    override val game: CraftanGame,
    val player: CraftanPlayer,
    // TODO: Missing structure coordinates
    val structureInfo: CraftanStructure,
) : CraftanGameEvent(game)
