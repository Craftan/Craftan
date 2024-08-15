package de.craftan

import de.craftan.commands.structureCommand
import de.craftan.config.ConfigSystem
import de.craftan.util.CraftanSystem
import de.craftan.util.SystemManager
import java.io.File
import java.nio.file.Files

object Craftan {
    val schematicsFolder = File("${PluginManager.dataFolder.absolutePath}/schematics")

    /**
     * Configures the Craftan plugin
     */
    fun configure() {
        createSchematicFolder()
        loadSystems()
        loadCommands()
    }

    private fun loadSystems() {
        addSystem(ConfigSystem)
    }

    private fun createSchematicFolder() {
        schematicsFolder.mkdirs()

        val stream = PluginManager.getResource("schematics/hexagon.schem") ?: return
        Files.copy(stream, File(schematicsFolder, "hexagon.schem").toPath())
    }

    private fun addSystem(system: CraftanSystem) {
        SystemManager.registerSystem(system)
    }

    private fun loadCommands() {
        structureCommand
    }
}
