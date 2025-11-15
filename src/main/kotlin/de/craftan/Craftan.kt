package de.craftan

import de.craftan.bridge.listener.PlayerJoinedLobbyListener
import de.craftan.commands.craftanCommand
import de.craftan.commands.structureCommand
import de.craftan.config.ConfigSystem
import de.craftan.io.CraftanEventBus
import de.craftan.io.MessageAdapter
import de.craftan.io.permissions.PermissionsAdapter
import de.craftan.util.CraftanSystem
import de.craftan.util.SystemManager
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.ormr.eventbus.EventBus
import java.io.File
import java.nio.file.Files

object Craftan {
    lateinit var scoreboardLibrary: ScoreboardLibrary

    val schematicsFolder = File("${PluginManager.dataFolder.absolutePath}/schematics")

    val logger = InternalMain.INSTANCE.logger

    /**
     * Craftans global event bus.
     * All events get passed through here.
     */
    val eventBus: CraftanEventBus = EventBus()

    /**
     * Configures the Craftan plugin
     */
    fun configure() {
        createSchematicFolder()
        loadFiles()
        loadSystems()
        loadCommands()

        PlayerJoinedLobbyListener()
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
