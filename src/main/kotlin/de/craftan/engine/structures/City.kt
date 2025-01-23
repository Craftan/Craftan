package de.craftan.engine.structures

import de.craftan.engine.CraftanResource
import de.craftan.engine.resources.WoodResource

class City : CraftanStructure {
    override val cost: Map<CraftanResource, Int> = mapOf((WoodResource to 2))
}
