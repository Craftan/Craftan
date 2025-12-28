package de.craftan.engine.structures

import de.craftan.engine.resources.CraftanResourceType


class Road : CraftanStructure {
    override val cost: Map<CraftanResourceType, Int> = mapOf((CraftanResourceType.LUMBER to 2))
}
