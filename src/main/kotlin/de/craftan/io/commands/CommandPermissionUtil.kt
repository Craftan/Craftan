package de.craftan.io.commands

import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack

object CommandPermissionUtil {
    val commandPermissions = mutableMapOf<ArgumentBuilder<CommandSourceStack, *>, CraftanCommand>()
}
