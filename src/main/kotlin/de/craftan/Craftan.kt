package de.craftan

import de.craftan.bridge.items.behaviors.DiceBehavior
import de.craftan.bridge.listeners.PlayerInteractedEntityEvent
import de.craftan.commands.craftanCommand
import de.craftan.commands.structureCommand
import de.craftan.config.ConfigSystem
import de.craftan.config.schema.CraftanConfig
import de.craftan.io.CraftanEventBus
import de.craftan.io.permissions.PermissionsAdapter
import de.craftan.util.CraftanSystem
import de.craftan.util.SystemManager
import de.staticred.kia.behaviour.registerBehaviors
import kotlinx.serialization.json.Json
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.ormr.eventbus.EventBus
import java.io.File
import java.nio.file.Files

object Craftan {
    lateinit var scoreboardLibrary: ScoreboardLibrary

    val schematicsFolder = File("${PluginManager.dataFolder.absolutePath}/schematics")

    val logger = InternalMain.INSTANCE.logger

    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    lateinit var config: () -> CraftanConfig

    /**
     * Craftans global event bus.
     * All events get passed through here.
     */
    val eventBus: CraftanEventBus = EventBus()

    /**
     * Configures the Craftan plugin
     */
    fun configure() {
        loadFiles()
        createSchematicFolder()
        loadSystems()
        loadCommands()
        registerItemBehaviors()

        PlayerInteractedEntityEvent().register()
    }

    private fun registerItemBehaviors() {
        registerBehaviors {
            +DiceBehavior.behavior
        }
    }

    private fun loadFiles() {
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
