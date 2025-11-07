package de.craftan

import de.craftan.commands.craftanCommand
import de.craftan.commands.structureCommand
import de.craftan.config.ConfigSystem
import de.craftan.io.MessageAdapter
import de.craftan.io.permissions.PermissionsAdapter
import de.craftan.util.CraftanSystem
import de.craftan.util.SystemManager
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary
import java.io.File
import java.nio.file.Files

object Craftan {
    lateinit var scoreboardLibrary: ScoreboardLibrary

    val schematicsFolder = File("${PluginManager.dataFolder.absolutePath}/schematics")

    /**
     * Configures the Craftan plugin
     */
    fun configure() {
        createSchematicFolder()
        loadFiles()
        loadSystems()
        loadCommands()
    }

    private fun loadFiles() {
        MessageAdapter.load()
        PermissionsAdapter.load()
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
    }
}
