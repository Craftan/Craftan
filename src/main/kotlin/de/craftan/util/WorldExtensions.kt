package de.craftan.util

import com.fastasyncworldedit.core.FaweAPI
import org.bukkit.World

fun World.toWorldEditWorld(): com.sk89q.worldedit.world.World {
    return FaweAPI.getWorld(name)
}