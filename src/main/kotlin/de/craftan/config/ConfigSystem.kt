package de.craftan.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import de.craftan.PluginManager
import de.craftan.util.CraftanSystem

object ConfigSystem : CraftanSystem {
    /**
     * Use this as global config for craftan
     */
    var config =
        ConfigLoaderBuilder
            .default()
            .withClassLoader(PluginManager.javaClass.classLoader)
            .addResourceSource("/config.toml")
            .build()
            .loadConfigOrThrow<CraftanConfig>()

    override fun load() {
        println(config.singleLobby)

        /**
         val file = File(PluginManager.dataFolder, "config.toml")

         if (!file.exists()) {
         file.parentFile.mkdirs()
         val defaultConfig = Config { addSpec(CraftanConfig) }
         file.createNewFile()

         defaultConfig.toToml.toFile(file)
         }

         config = Config { addSpec(CraftanConfig) }.from.toml.watchFile(file)
         **/
    }
}
