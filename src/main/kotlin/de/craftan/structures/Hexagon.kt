package de.craftan.structures

import com.sk89q.worldedit.extent.clipboard.Clipboard
import de.craftan.Craftan
import java.io.File

val hexagonSchematic = File(Craftan.schematicsFolder, "hexagon.schem")
val hexagonStructure: Clipboard = loadStructureToClipboard(hexagonSchematic) ?: error("Hexagon schematic could not been loaded")
