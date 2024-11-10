package de.craftan.io.permissions.commands

class CraftanCommandPermission(
    permission: String,
) : CommandPermission("craftan.$permission")
