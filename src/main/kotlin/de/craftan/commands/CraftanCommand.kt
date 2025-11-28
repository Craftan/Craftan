package de.craftan.commands

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import de.craftan.bridge.inventory.config.CraftanGameConfigManager
import de.craftan.bridge.inventory.config.configureCraftanGameInventory
import de.craftan.bridge.items.behaviors.DiceBehavior
import de.craftan.bridge.lobby.CraftanLobbyManager
import de.craftan.bridge.util.sendNotification
import de.craftan.io.*
import de.craftan.io.commands.craftanCommand
import de.craftan.io.commands.craftanSubCommand
import de.craftan.io.commands.to
import de.staticred.kia.inventory.builder.kItem
import de.staticred.kia.inventory.extensions.openInventory
import io.papermc.paper.datacomponent.DataComponentTypes
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestList
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material

val craftanCommand =
    craftanCommand("craftan", "Manage all configuration and settings of craftan") {

        craftanSubCommand("item", "get an item with the given model") {
            argument<String>("model", StringArgumentType.string()) {
                runs {
                    val item = kItem(Material.WHITE_CONCRETE) {
                        model = Key.key(getArgument<String>("model"))
                        setDisplayName(Component.text("Dice").color(TextColor.color(0x00F0FF)))

                        addBehavior(DiceBehavior.behavior)
                    }.toItemStack()

                    player.inventory.addItem(item)
                }
            }
        }

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
                            CraftanPlaceholder.LOBBY_ID to literalText { it.key },
                            CraftanPlaceholder.CURRENT_PLAYERS to literalText { it.value.players().size },
                            CraftanPlaceholder.MAX_PLAYERS to literalText("4"),
                            CraftanPlaceholder.CURRENT_MAP to literalText("Default")
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
