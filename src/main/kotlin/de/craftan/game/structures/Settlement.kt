package de.craftan.game.structures

import de.craftan.game.CraftanResource
import de.craftan.game.CraftanStructure
import de.craftan.game.resources.WoodResource

class Settlement : CraftanStructure {
    override val cost: Map<CraftanResource, Int> = mapOf((WoodResource to 2))
}
