package de.craftan.config

import de.craftan.Craftan
import de.craftan.io.config.Configs
import de.craftan.util.CraftanSystem

object ConfigSystem : CraftanSystem {

    override fun load() {
        Craftan.logger.info("[ConfigSystem] Initializing CraftanConfig live provider via Configs")
        val handle = Configs.of<CraftanConfig>()
        Craftan.config = handle::get
        // Perform an initial read to ensure file exists and is updated if necessary
        handle.get()
    }
}
