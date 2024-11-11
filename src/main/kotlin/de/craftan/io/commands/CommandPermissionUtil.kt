package de.craftan.io.commands

import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack

/**
 * Util class to hold craftan command tree
 */
object CommandPermissionUtil {
    val commandPermissions = mutableMapOf<ArgumentBuilder<CommandSourceStack, *>, CraftanCommand>()
}
