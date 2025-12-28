package de.craftan.bridge.commands

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import de.craftan.Craftan
import de.craftan.bridge.inventory.config.CraftanGameConfigManager
import de.craftan.bridge.inventory.config.configureCraftanGameInventory
import de.craftan.bridge.lobby.CraftanLobbyManager
import de.craftan.bridge.util.sendNotification
import de.craftan.config.ConfigSystem
import de.craftan.config.CraftanConfigs
import de.craftan.io.*
import de.craftan.io.commands.craftanCommand
import de.craftan.io.commands.craftanSubCommand
import de.craftan.io.commands.to
import de.staticred.kia.inventory.extensions.openInventory
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestList

val craftanCommand =
    craftanCommand("craftan", "Manage all configuration and settings of craftan") {
        craftanSubCommand("lobby", "manage current craftan lobbies") {
            craftanSubCommand("list", "list all current lobbies") {
                runs {
                    val lobbies = CraftanLobbyManager.listLobbies()

                    if (lobbies.isEmpty()) {
                        player.sendNotification(CraftanNotification.LIST_LOBBIES_EMPTY)
                        return@runs
                    }

                    player.sendNotification(CraftanNotification.LIST_LOBBIES_INTRO)

                    lobbies.forEach {
                        player.sendMessage(CraftanNotification.LIST_LOBBIES_ENTRY.resolveWithPlaceholder(player, mapOf(
                            CraftanPlaceholder.LOBBY_ID to literalText(it.value.id.toString()),
                            CraftanPlaceholder.CURRENT_PLAYERS to literalText(it.value.players().size.toString()),
                            CraftanPlaceholder.MAX_PLAYERS to literalText(it.value.maxPlayers.toString()),
                            CraftanPlaceholder.CURRENT_MAP to literalText(it.value.board.layout.name)
                        )))
                    }
                }
            }

            craftanSubCommand("create", "Create a new lobby") {
                runs {
                    if (CraftanLobbyManager.isInLobby(player)) {
                        player.sendNotification(CraftanNotification.ALREADY_IN_LOBBY)
                        return@runs
                    }
                    player.openInventory(configureCraftanGameInventory(player))
                }
            }
            craftanSubCommand("clear", "Clear your current lobby configuration") {
                runs {
                    CraftanGameConfigManager.clearPlayer(player)
                    player.sendNotification(CraftanNotification.LOBBY_CONFIG_CLEARED)
                }
            }
            craftanSubCommand("leave", "Leave your current lobby") {
                runs {
                    val lobby = CraftanLobbyManager.getLobbyForPlayer(player)
                    if (lobby == null) {
                        player.sendNotification(CraftanNotification.NOT_IN_LOBBY)
                        return@runs
                    }

                    CraftanLobbyManager.removePlayerFromLobby(player)
                    player.sendNotification(CraftanNotification.LOBBY_LEFT)
                }
            }
            craftanSubCommand("start", "starts the game") {
                runs {
                    val lobby = CraftanLobbyManager.getLobbyForPlayer(player)
                    if (lobby == null) {
                        player.sendNotification(CraftanNotification.NOT_IN_LOBBY)
                        return@runs
                    }

                    lobby.startCountdown()
                }
            }
            craftanSubCommand("stop", "stops the game") {
                argument<Int>("lobby_id", IntegerArgumentType.integer(0)) {
                    suggestList {
                        CraftanLobbyManager.listLobbies().keys.toList()
                    }
                    runs {
                        val lobby = CraftanLobbyManager.getLobbyById(getArgument("lobby_id"))
                        if (lobby == null) {
                            player.sendNotification(CraftanNotification.LOBBY_NOT_FOUND)
                            return@runs
                        }

                        lobby.close()
                        player.sendNotification(CraftanNotification.EXTERNAL_LOBBY_CLOSED)
                    }
                }
            }
        }

        craftanSubCommand("config", "Manage all config files of craftan") {
            craftanSubCommand("reload", "reload all config files from the configuration") {
                runs {
                    player.sendMessage(CraftanNotification.RELOAD_FILES_START.resolve(player))
                    ConfigSystem.reload()
                    player.sendMessage(CraftanNotification.RELOAD_FILES_FINISH.resolve(player))
                }
            }
            craftanSubCommand("read", "Read a value from the config") {
                for (config in CraftanConfigs.configs()) {
                    craftanSubCommand(config, config) {
                        argument<String>("Path") {
                            suggestList {
                                CraftanConfigs.valuesForConfig(config)
                            }

                            runs {
                                val path = getArgument<String>("Path")

                                val value = CraftanConfigs.getValueForConfig(config, path)

                                if (value == null) {
                                    player.sendMessage(CraftanNotification.CONFIG_NO_VALUE_FOUND.resolveWithPlaceholder(player, mapOf(
                                        CraftanPlaceholder.KEY to literalText(path),
                                        CraftanPlaceholder.CONFIG to literalText(config)
                                    )))
                                    return@runs
                                }
                                player.sendMessage(CraftanNotification.CONFIG_VALUE.resolveWithPlaceholder(player, mapOf(
                                    CraftanPlaceholder.KEY to literalText(path),
                                    CraftanPlaceholder.CONFIG to literalText(config),
                                    CraftanPlaceholder.VALUE to literalText(value.toString())
                                )))
                            }
                        }
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
