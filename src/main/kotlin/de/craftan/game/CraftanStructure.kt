package de.craftan.game

interface CraftanStructure {
    /**
     * Cost to build this given structure
     */
    val cost: Map<CraftanResource, Int>
}
