package de.craftan.commands

import com.fastasyncworldedit.core.FaweAPI
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.bridge.lobby.CraftanBoard
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.bridge.lobby.craftanDefaultSettings
import de.craftan.bridge.map.CraftanMap
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.MutableCraftanGameConfig
import de.craftan.engine.map.maps.DefaultMapLayout
import de.craftan.structures.loadStructureToClipboard
import de.craftan.structures.placeStructure
import net.axay.kspigot.commands.*
import java.io.File

val structureCommand =
    command("structure") {
        literal("load") {
            argument("structure", StringArgumentType.string()) {
                suggestList {
                    val folder = Craftan.schematicsFolder
                    folder.listFiles()?.toList()?.map { it.name } ?: listOf()
                }

                runs {
                    val structure = getArgument<String>("structure")
                    val location = player.location

                    if (!structure.endsWith(".schem")) {
                        player.sendMessage("File must end with .schem")
                        return@runs
                    }

                    val schematic = File(Craftan.schematicsFolder, structure)
                    val clipboard = loadStructureToClipboard(schematic) ?: return@runs player.sendMessage("Invalid schematic")
                    val world = FaweAPI.getWorld(player.world.name)

                    placeStructure(BlockVector3.at(location.x, location.y, location.z), world, clipboard)

                    player.sendMessage("Pasted given schematic")
                }
            }
        }

        literal("hexgrid") {
            argument("spacing", IntegerArgumentType.integer(0)) {
                runs {
                    val spacing = getArgument<Int>("spacing")

                    val location = player.location
                    val world = FaweAPI.getWorld(player.world.name)
                    val center = BlockVector3.at(location.x, location.y, location.z)
                    val lobby = CraftanLobby(CraftanBoard(world, center, spacing, DefaultMapLayout()), MutableCraftanGameConfig().toCraftanGameConfig())
                    player.sendMessage("Building map...")
                    CraftanMap(lobby).build()
                }
            }
        }
    }
