package de.craftan

import de.craftan.bridge.commands.craftanCommand
import de.craftan.bridge.items.behaviors.DiceBehavior
import de.craftan.bridge.listener.bukkit.PlayerInteractedEntityEvent
import de.craftan.bridge.listener.bukkit.PlayerJoinedLeftListener
import de.craftan.bridge.lobby.listeners.CraftanLobbyListeners
import de.craftan.commands.structureCommand
import de.craftan.config.ConfigSystem
import de.craftan.config.CraftanConfigs
import de.craftan.database.DatabaseSystem
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

    lateinit var configs: CraftanConfigs

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
        registerListeners()
    }

    private fun registerListeners() {
        CraftanLobbyListeners.register()
        PlayerInteractedEntityEvent().register()
        PlayerJoinedLeftListener().register()
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
        addSystem(DatabaseSystem)
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
