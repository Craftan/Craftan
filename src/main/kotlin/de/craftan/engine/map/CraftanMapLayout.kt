package de.craftan.engine.map

interface CraftanMapLayout {
    /**
     * Abstract name of this map layout, for example: default no water
     */
    val name: String

    /**
     * The map this layout is based on
     */
    val map: CraftanMap
}
