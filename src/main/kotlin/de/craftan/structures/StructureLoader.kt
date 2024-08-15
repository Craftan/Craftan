package de.craftan.structures

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.World
import java.io.File

/**
 * Loads a schematic from the given file into the returned clipboard
 * @param schematic file containing schematic
 * @return Clipboard, or null when the file is invalid
 */
fun loadStructureToClipboard(schematic: File): Clipboard? {
    val format = ClipboardFormats.findByFile(schematic) ?: return null
    val reader = format.getReader(schematic.inputStream())
    return reader.use { it.read() }
}

/**
 * Places the given clipboard into the world at the given location
 * @param location xyz of the origin
 * @param world the world
 * @param structure the clipboard to place
 */
fun placeStructure(
    location: BlockVector3,
    world: World,
    structure: Clipboard,
) {
    val session = WorldEdit.getInstance().newEditSession(world)

    session.use {
        val operation =
            ClipboardHolder(structure)
                .createPaste(it)
                .ignoreAirBlocks(true)
                .to(location)
                .build()
        Operations.complete(operation)
    }
}
