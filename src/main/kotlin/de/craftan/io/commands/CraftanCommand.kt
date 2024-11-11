package de.craftan.io.commands

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.resolveWithPlaceholder
import net.axay.kspigot.commands.*
import net.minecraft.commands.CommandSourceStack

/**
 * Models a helper class, to build craftan commands with automatic permission generation
 *
 * @param name will be used to generate the name of this command. /<name>
 * @param description will be used to generate the description of the command
 *
 * Description of commands will be build the following:
 * - commands will be appended with the "info" subcommand to get the information about the command
 * - /<name> info - will then return the information about the command, and how to build it.
 * - permission: "craftan.cmd.$name"
 *
 * @return returns the command builder
 */
fun craftanCommand(
    name: String,
    description: String,
    builder: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit,
): LiteralArgumentBuilder<CommandSourceStack> {
    val permission = "craftan.cmd.$name"
    val command =
        command(name) {
            requiresPermission(permission)
            infoSubCommand(name, description)
        }

    CommandPermissionUtil.commandPermissions[command] = CraftanCommand(name, description, permission)
    command.apply(builder)

    return command
}

/**
 * Builds a sub command for a craftan command and generates the required info and permission command
 * @param name of the sub command
 * @param description of the sub command
 * @param builder command builder
 *
 * Builds the sub command based on the parent command.
 * THE PARENT COMMAND NEEDS TO BE BUILT USING [craftanCommand] for this to work
 *
 * will generate the permission and info sub command for this command
 * - permission: "$parentCommand.$name"
 */
inline fun ArgumentBuilder<CommandSourceStack, *>.craftanSubCommand(
    name: String,
    description: String,
    builder: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {},
) {
    val parent = CommandPermissionUtil.commandPermissions[this] ?: error("Parent command not found. Use <craftanCommand()> to build a craftan sub command!")

    val computedName = "${parent.name} $name"

    val permission = parent.permission + ".$name"

    val subCommand =
        command(name, false) {
            requiresPermission(permission)
            infoSubCommand(computedName, description)
        }

    val cc = CraftanCommand(name, description, permission, parent)
    CommandPermissionUtil.commandPermissions[subCommand] = cc

    subCommand.apply(builder)
    then(subCommand)
}

/**
 * Builds an <info> sub command
 * @param name of the parent command
 * @param description of the parent command
 * @param builder additional information builder
 * @return Builder with attached sub command
 */
inline fun ArgumentBuilder<CommandSourceStack, *>.infoSubCommand(
    name: String,
    description: String,
    builder: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {},
) = command("info", false, builder)
    .apply {
        runs {
            val commandArgs = arguments.map { it.usageText }
            val information =
                CraftanNotification.INFORMATION_FORMATING
                    .resolveWithPlaceholder(
                        player,
                        mapOf(
                            CraftanPlaceholder.COMMAND_ARGS to commandArgs.joinToString(" "),
                            CraftanPlaceholder.COMMAND_NAME to name,
                            CraftanPlaceholder.COMMAND_DESCRIPTION to description,
                        ),
                    )

            player.sendMessage(information)
        }
    }.also { then(it) }

/**
 * Holds the information of a craftan command
 * @see de.craftan.io.commands.CommandPermissionUtil.commandPermissions
 */
data class CraftanCommand(
    val name: String,
    val description: String,
    val permission: String,
    val parent: CraftanCommand? = null,
)
