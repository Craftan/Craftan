package de.craftan.engine

interface CraftanStructure {
    /**
     * Cost to build this given structure
     */
    val cost: Map<CraftanResource, Int>
}
