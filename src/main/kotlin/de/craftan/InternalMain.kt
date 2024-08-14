package de.craftan

import de.craftan.util.SystemManager
import net.axay.kspigot.main.KSpigot

class InternalMain : KSpigot() {
    companion object {
        lateinit var INSTANCE: InternalMain
            private set
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        Craftan.configure()
        SystemManager.loadSystems()
    }
}

val PluginManager by lazy { InternalMain.INSTANCE }
