package de.craftan

import de.craftan.commands.craftanCommand
import de.craftan.commands.structureCommand
import de.craftan.commands.testCommand
import de.craftan.config.ConfigSystem
import de.craftan.io.MessageAdapter
import de.craftan.util.CraftanSystem
import de.craftan.util.SystemManager
import java.io.File
import java.nio.file.Files
import kotlin.io.path.exists

object Craftan {
    val schematicsFolder = File("${PluginManager.dataFolder.absolutePath}/schematics")

    /**
     * Configures the Craftan plugin
     */
    fun configure() {
        createSchematicFolder()
        loadSystems()
        loadCommands()
        loadMessages()
    }

    private fun loadMessages() {
        MessageAdapter.load()
    }

    private fun loadSystems() {
        addSystem(ConfigSystem)
    }

    private fun createSchematicFolder() {
        schematicsFolder.mkdirs()

        val stream = PluginManager.getResource("schematics/hexagon.schem") ?: return

        val hexFile = File(schematicsFolder, "hexagon.schem")

        if (!hexFile.exists()) {
            Files.copy(stream, hexFile.toPath())
        }
    }

    private fun addSystem(system: CraftanSystem) {
        SystemManager.registerSystem(system)
    }

    private fun loadCommands() {
        structureCommand
        craftanCommand
        testCommand
    }
}
