package de.craftan.engine.structures

import de.craftan.engine.resources.CraftanResourceType

interface CraftanStructure {
    /**
     * Cost to build this given structure
     */
    val cost: Map<CraftanResourceType, Int>
}
