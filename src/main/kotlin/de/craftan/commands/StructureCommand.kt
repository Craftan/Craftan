package de.craftan.commands

import com.fastasyncworldedit.core.FaweAPI
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.sk89q.worldedit.math.BlockVector3
import de.craftan.Craftan
import de.craftan.structures.hexagonStructure
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
            argument("spacing", IntegerArgumentType.integer(0))
            argument("rings", IntegerArgumentType.integer(0))
            runs {
                val spacing = getArgument<Int>("spacing")
                val rings = getArgument<Int>("rings")

                val world = FaweAPI.getWorld(player.world.name)
                val location = player.location
                val center = BlockVector3.at(location.x, location.y, location.z)

                placeStructure(center, world, hexagonStructure)

                val hexSize = 35
                val hexToSide = 18

                val lines = rings * 2 + 1

                for (currentLine in 0..lines) {
                }
            }
        }
    }
