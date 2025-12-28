package de.craftan.engine.structures

import de.craftan.engine.resources.CraftanResourceType

class Settlement : CraftanStructure {
    override val cost: Map<CraftanResourceType, Int> = mapOf((CraftanResourceType.WOOL to 2))
}
