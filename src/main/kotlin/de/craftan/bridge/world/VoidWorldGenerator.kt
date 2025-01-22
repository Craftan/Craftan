package de.craftan.bridge.world

import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

/**
 * @author Devin Fritz
 */
class VoidWorldGenerator: ChunkGenerator() {

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
    }

    override fun shouldGenerateStructures(): Boolean {
        return false
    }

    override fun shouldGenerateNoise(): Boolean {
        return false
    }

    override fun shouldGenerateSurface(): Boolean {
        return false
    }

    override fun shouldGenerateCaves(): Boolean {
        return false
    }

    override fun shouldGenerateDecorations(): Boolean {
        return false
    }

    override fun shouldGenerateMobs(): Boolean {
        return false
    }
}