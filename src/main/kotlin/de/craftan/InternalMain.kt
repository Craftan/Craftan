package de.craftan

import de.craftan.util.SystemManager
import de.staticred.kia.KIA
import net.axay.kspigot.main.KSpigot
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary

class InternalMain : KSpigot() {
    companion object {
        lateinit var INSTANCE: InternalMain
            private set
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        Craftan.scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(this)

        KIA.create(this, true)
        Craftan.configure()
        SystemManager.loadSystems()
    }

    override fun shutdown() {
        Craftan.scoreboardLibrary.close();
    }
}

val PluginManager by lazy { InternalMain.INSTANCE }
