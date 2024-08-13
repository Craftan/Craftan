package de.craftan

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
    }
}

val Manager by lazy { InternalMain.INSTANCE }
