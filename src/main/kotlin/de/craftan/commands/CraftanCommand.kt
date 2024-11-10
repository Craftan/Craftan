package de.craftan.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import de.craftan.io.*
import de.craftan.io.permissions.commands.CommandPermission
import de.craftan.io.permissions.commands.CraftanCommandPermission
import net.axay.kspigot.commands.*
import net.minecraft.commands.CommandSourceStack

val craftanCommand =
    command("craftan") {
        literal("messages") {
            literal("reload") {
                runs {
                    player.sendMessage(CraftanNotification.RELOAD_FILES_START.resolve(player))
                    MessageAdapter.load()
                    player.sendMessage(CraftanNotification.RELOAD_FILES_FINISH.resolve(player))
                }
            }

            literal("load") {
                requiresPermission(CraftanCommandPermission("load"))

                argument<String>("notification", StringArgumentType.string()) {
                    suggestList {
                        CraftanNotification.entries.map { it.name }
                    }

                    runs {
                        val notificationName = getArgument<String>("notification")
                        val notification = CraftanNotification.entries.firstOrNull { it.name == notificationName }

                        if (notification == null) {
                            return@runs player.sendMessage("The given notification was not found!")
                        }

                        val configuredMessage = notification.resolve(player)
                        player.sendMessage(configuredMessage)
                    }

                    argument<String>("locale", StringArgumentType.string()) {
                        runs {
                            val notificationName = getArgument<String>("notification")
                            val localeName = getArgument<String>("locale")
                            val notification = CraftanNotification.entries.firstOrNull { it.name == notificationName }

                            if (notification == null) {
                                return@runs player.sendMessage("The given notification was not found!")
                            }

                            val configuredMessage = notification.resolve(localeName)
                            player.sendMessage(configuredMessage)
                        }
                    }
                }
            }
        }

        literal("locales") {
            requiresPermission(CraftanCommandPermission("locale"))
            literal("list") {
                runs {
                    val locales = MessageAdapter.getResolvedLocales()
                    val localeNotification = CraftanNotification.LOCALES.resolveWithPlaceholder(player, mapOf(CraftanPlaceholder.LOCALES to locales.joinToString(",") { it }))
                    player.sendMessage(localeNotification)
                }
            }
        }
    }

fun ArgumentBuilder<CommandSourceStack, *>.requiresPermission(commandPermission: CommandPermission) {
    this.requiresPermission(commandPermission.buildPermission())
}
