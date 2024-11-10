package de.craftan.engine.structures

import de.craftan.engine.CraftanResource

interface CraftanStructure {
    /**
     * Cost to build this given structure
     */
    val cost: Map<CraftanResource, Int>
}
