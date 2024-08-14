package de.craftan.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml
import de.craftan.PluginManager
import de.craftan.util.CraftanSystem
import java.io.File

object ConfigSystem : CraftanSystem {
    /**
     * Use this as global config for craftan
     */
    lateinit var config: Config

    override fun load() {
        val file = File(PluginManager.dataFolder, "config.toml")

        if (!file.exists()) {
            file.parentFile.mkdirs()
            val defaultConfig = Config { addSpec(CraftanConfig) }
            file.createNewFile()

            defaultConfig.toToml.toFile(file)
        }

        config = Config { addSpec(CraftanConfig) }.from.toml.watchFile(file)
    }
}
