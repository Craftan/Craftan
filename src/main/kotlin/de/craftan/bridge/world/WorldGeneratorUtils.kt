package de.craftan.bridge.world

import org.bukkit.World
import org.bukkit.WorldCreator

fun generateEmptyWorld(name: String): World {
    val creator = WorldCreator(name)
    creator.generator(VoidWorldGenerator())

    val world = creator.createWorld() ?: error("Error while creating world!")
    return world
}