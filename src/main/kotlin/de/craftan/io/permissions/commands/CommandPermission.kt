package de.craftan.io.permissions.commands

import de.craftan.io.permissions.CraftanPermission

abstract class CommandPermission(
    val commandPermission: String,
) : CraftanPermission("cmd.$commandPermission")
