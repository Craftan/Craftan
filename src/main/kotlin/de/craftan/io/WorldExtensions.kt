package de.craftan.io

import de.craftan.Craftan
import net.axay.kspigot.extensions.server
import net.axay.kspigot.runnables.task
import org.apache.commons.io.FileUtils
import org.bukkit.World

fun World.unloadAndDelete() {
    if (server.isStopping) return

    task(
        true,
        40L,
    ) {
        Craftan.logger.info("Unloading world $name")

        val result = server.unloadWorld(this, false)

        if (!result) {
            Craftan.logger.warning("Failed to save world $name")
            return@task
        }

        Craftan.logger.info("Deleting world $name")
        runCatching { FileUtils.deleteDirectory(worldFolder) }.onFailure {
            Craftan.logger.info("Failed deleting world $name")
            it.printStackTrace()
        }
    }
}