package de.craftan.engine.map

data class LayoutRow<T>(
    val tiles: List<T>,
)

fun <T> layoutRow(vararg tiles: T): LayoutRow<T> = LayoutRow<T>(tiles.toList())
