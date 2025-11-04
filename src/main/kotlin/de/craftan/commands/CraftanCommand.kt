package de.craftan.commands

import com.mojang.brigadier.arguments.StringArgumentType
import de.craftan.Craftan
import de.craftan.bridge.lobby.LobbyManager
import de.craftan.bridge.util.sendNotification
import de.craftan.io.*
import de.craftan.io.commands.craftanCommand
import de.craftan.io.commands.craftanSubCommand
import de.craftan.io.commands.to
import net.axay.kspigot.commands.*

val craftanCommand =
    craftanCommand("craftan", "Manage all configuration and settings of craftan") {
        craftanSubCommand("lobby", "manage current craftan lobbies") {
            craftanSubCommand("list", "list all current lobbies") {
                runs {
                    val lobbies = LobbyManager.listLobbies()

                    if (lobbies.isEmpty()) {
                        player.sendMessage(CraftanNotification.LIST_LOBBIES_EMPTY.resolve(player))
                        return@runs
                    }
                }
            }
        }

        craftanSubCommand("messages", "Manage the localization of craftan's messages") {
            craftanSubCommand("reload", "reload all messages from the configuration") {
                runs {
                    player.sendMessage(CraftanNotification.RELOAD_FILES_START.resolve(player))
                    MessageAdapter.load()
                    player.sendMessage(CraftanNotification.RELOAD_FILES_FINISH.resolve(player))
                }
            }

            craftanSubCommand("load", "loads the given argument from the players localization.") {
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

        craftanSubCommand("locales", "manage the localization files of craftan") {
            craftanSubCommand("list", "list all registered locales") {
                runs {
                    val locales = MessageAdapter.getResolvedLocales()
                    val localeNotification = CraftanNotification.LOCALES.resolveWithPlaceholder(player, mapOf(CraftanPlaceholder.LOCALES to locales.joinToString(",") { it }))
                    player.sendMessage(localeNotification)
                }
            }
            craftanSubCommand("create", "create a new localization language file") {
                argument<String>("locale") {
                    runs {
                        val locale = getArgument<String>("locale")

                        if (MessageAdapter.getResolvedLocales().contains(locale)) {
                            player.sendNotification(CraftanNotification.LOCALES_CREATE_FAILED)
                            return@runs
                        }

                        MessageAdapter.createLocaleFromDefault(locale)
                        player.sendNotification(CraftanNotification.LOCALES_CREATE_SUCCESS)
                    }
                }
            }
        }
    }
