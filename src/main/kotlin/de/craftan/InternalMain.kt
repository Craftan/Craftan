package de.craftan

import de.craftan.util.SystemManager
import de.staticred.kia.KIA
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
        KIA.create(this, true)
        Craftan.configure()
        SystemManager.loadSystems()
    }
}

val PluginManager by lazy { InternalMain.INSTANCE }
