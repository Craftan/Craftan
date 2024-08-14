package de.craftan

import de.craftan.config.ConfigSystem
import de.craftan.util.CraftanSystem
import de.craftan.util.SystemManager

object Craftan {
    /**
     * Configures the Craftan plugin
     */
    fun configure() {
        addSystem(ConfigSystem)
    }

    private fun addSystem(system: CraftanSystem) {
        SystemManager.registerSystem(system)
    }
}
