package de.craftan.engine.map

import de.craftan.engine.resources.CraftanResourceType

enum class MapMaterialType(
    val resourceType: CraftanResourceType?
) {
    WHEAT(CraftanResourceType.GRAIN),
    STONE(CraftanResourceType.ORE),
    SHEEP(CraftanResourceType.WOOL),
    BRICK(CraftanResourceType.BRICK),
    WOOD(CraftanResourceType.LUMBER),
    DESERT(null),
}
