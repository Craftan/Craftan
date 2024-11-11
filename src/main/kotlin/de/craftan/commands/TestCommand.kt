package de.craftan.commands

import de.craftan.io.commands.craftanCommand
import net.axay.kspigot.commands.runs

val testCommand =
    craftanCommand("test", "this is a test command") {
        runs {
            player.sendMessage("this runs")
        }
    }
