package de.craftan.io

import de.craftan.Craftan
import de.craftan.PluginManager
import de.craftan.config.LanguageFile
import de.craftan.io.config.ConfigFile
import de.craftan.io.config.Configs
import java.io.File

/**
 * Small helper around ConfigFile for Craftan language YAMLs.
 */
object LanguageFiles {
    private const val DIR = "language"

    fun pathFor(locale: String): String = "$DIR/$locale.yml"

    fun handle(locale: String, defaultMessages: Map<String, String>): ConfigFile<LanguageFile> {
        val file = File(PluginManager.dataFolder, pathFor(locale))
        Craftan.logger.fine("[LanguageFiles] Creating handle for locale '$locale' at ${file.absolutePath}")
        val defaults = LanguageFile(locale = locale, messages = defaultMessages)
        return Configs.of(LanguageFile::class, file, defaults)
    }

    /**
     * Lists existing locale codes by scanning the language folder for *.yml files,
     * excluding backups like *.bak-<ts>.yml or *.yml.backup.
     */
    fun listLocales(): List<String> {
        val folder = File(PluginManager.dataFolder, DIR)
        Craftan.logger.fine("[LanguageFiles] Scanning locales in ${folder.absolutePath}")
        val files = folder.listFiles()?.toList() ?: emptyList()
        val result = files.filter { file ->
            val isYml = file.extension == "yml"
            val isLegacyBackup = file.name.contains(".bak-") && file.name.endsWith(".yml")
            val isNewBackup = file.name.endsWith(".yml.backup")
            isYml && !isLegacyBackup && !isNewBackup
        }.map { it.nameWithoutExtension }
        Craftan.logger.fine("[LanguageFiles] Discovered locales: ${result.joinToString(", ")}")
        return result
    }
}
