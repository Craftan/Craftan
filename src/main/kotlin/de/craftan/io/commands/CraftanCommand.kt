package de.craftan.io.commands

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.craftan.bridge.util.toComponent
import de.craftan.io.CraftanNotification
import de.craftan.io.CraftanPlaceholder
import de.craftan.io.permissions.CraftanPermission
import de.craftan.io.resolveWithPlaceholder
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.bukkit.plainText
import net.kyori.adventure.text.Component
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
            requiresPermission(CraftanPermission(permission, "Required for command $name"))
        }

    val cc = CraftanCommand(name, name, description, permission)
    CommandPermissionUtil.commandPermissions[command] = cc
    command.apply(builder)
    command.apply { infoSubCommand(cc) }

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
    val initParent = CommandPermissionUtil.commandPermissions[this] ?: error("Parent command for $name not found. Use <craftanCommand()> to build a craftan sub command!")

    var currParent = initParent
    var parentCommandName = currParent.name

    while (currParent.parent != null) { // walk through parents until root is found
        currParent = currParent.parent!!
        parentCommandName = currParent.name + " $parentCommandName"
    }

    val computedName = "$parentCommandName $name"

    val permission = currParent.permission + ".$name"

    val cc = CraftanCommand(name, computedName, description, permission, initParent)

    val subCommand =
        command(name, false) {
            requiresPermission(CraftanPermission(permission, "Require for command $name"))
            infoSubCommand(cc)
        }

    initParent.subCommands += cc
    CommandPermissionUtil.commandPermissions[subCommand] = cc

    subCommand.apply(builder)
    this.then(subCommand)
}

/**
 * Builds an <info> sub command
 * @param cc command to build from
 * @param builder additional information builder
 * @return Builder with attached sub command
 */
fun ArgumentBuilder<CommandSourceStack, *>.infoSubCommand(
    cc: CraftanCommand,
    builder: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {},
) = command("info", false) {}
    .apply(builder)
    .apply {
        runs {
            val commandArgs = arguments.map { it.usageText }

            val subcommands = cc.subCommands

            val message = if (subcommands.isEmpty()) CraftanNotification.COMMAND_SUBCOMMANDS_NONE else CraftanNotification.COMMAND_SUBCOMMANDS_COUNT

            val subCommandInfo = message.resolveWithPlaceholder(player, mapOf(CraftanPlaceholder.COUNT to "${subcommands.size}"))

            val subCommandsComponent = Component.text(subcommands.joinToString("\n") { "- ${it.name}${if (it.subCommands.isNotEmpty()) " - and ${it.subCommands.size} more" else "" }" })

            val information =
                CraftanNotification.INFORMATION_FORMATING
                    .resolveWithPlaceholder(
                        player,
                        mapOf(
                            CraftanPlaceholder.COMMAND_ARGS to commandArgs.joinToString(" ") { it },
                            CraftanPlaceholder.COMMAND_NAME to cc.fullName,
                            CraftanPlaceholder.COMMAND_DESCRIPTION to cc.description,
                            CraftanPlaceholder.COMMAND_SUBCOMMAND_INFO to subCommandInfo.plainText(),
                            CraftanPlaceholder.COMMAND_SUBCOMMANDS to subCommandsComponent,
                        ),
                    )

            player.sendMessage(information)
        }
    }.also { then(it) }

/**
 * Holds the information of a craftan command
 * @see de.craftan.io.commands.CommandPermissionUtil.commandPermissions
 * @param name single of the command
 * @param fullName of the command, including parents
 * @param description of the command
 * @param permission required to execute this command
 * @param parent if command is not a root
 * @param subCommands list of subcommands
 */
data class CraftanCommand(
    val name: String,
    val fullName: String,
    val description: String,
    val permission: String,
    val parent: CraftanCommand? = null,
    val subCommands: MutableList<CraftanCommand> = mutableListOf(),
)

/**
 * Maps A to the given string. The string will be converted to a valid component
 * @param A type a to map
 * @param that the string to map to
 * @return Pair returning a mapping to the component
 */
infix fun <A> A.to(that: String): Pair<A, Component> = Pair(this, that.toComponent())

fun ArgumentBuilder<CommandSourceStack, *>.requiresPermission(permission: CraftanPermission): ArgumentBuilder<*, *> =
    requires {
        it.bukkitSender.hasPermission(permission.permission)
    }
