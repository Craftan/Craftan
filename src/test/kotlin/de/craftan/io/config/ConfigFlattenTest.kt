package de.craftan.io.config

import de.craftan.config.LanguageFile
import org.bukkit.configuration.file.YamlConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

data class FlattenedMapConfig(
    val locale: String = default("en_US"),
    val messages: Map<String, String> = flatten<Map<String, String>>().default(emptyMap())
) : CraftanConfig("lang.yml")

class ConfigFlattenTest {
    @Test
    fun `test flattened map reading with nested yaml`() {
        val tempDir = Files.createTempDirectory("craftan-flatten-test").toFile()
        val configFile = File(tempDir, "lang.yml")
        
        val yaml = YamlConfiguration()
        yaml.set("locale", "de_DE")
        yaml.set("lobby.player.joined", "Willkommen")
        yaml.set("cmd.help", "Hilfe")
        yaml.save(configFile)
        
        val configHandle = Configs.of(FlattenedMapConfig::class, configFile)
        val config = configHandle.get()
        
        assertEquals("de_DE", config.locale)
        assertEquals("Willkommen", config.messages["lobby.player.joined"])
        assertEquals("Hilfe", config.messages["cmd.help"])
        assertEquals(2, config.messages.size)
    }

    data class Inner(val field: String = default("default"))
    data class ComplexConfig(
        val other: Inner = default(Inner()),
        val messages: Map<String, String> = flatten<Map<String, String>>().default(emptyMap())
    ) : CraftanConfig("complex.yml")

    @Test
    fun `test flattened map excludes nested keys of other properties`() {
        val tempDir = Files.createTempDirectory("craftan-complex-test").toFile()
        val configFile = File(tempDir, "complex.yml")
        
        val yaml = YamlConfiguration()
        yaml.set("other.field", "custom")
        yaml.set("lobby.message", "Hi")
        yaml.save(configFile)
        
        val configHandle = Configs.of(ComplexConfig::class, configFile)
        val config = configHandle.get()
        
        assertEquals("custom", config.other.field)
        assertEquals("Hi", config.messages["lobby.message"])
        assertEquals(1, config.messages.size)
        // Ensure "other.field" is NOT in messages
        assertEquals(null, config.messages["other.field"])
    }

    @Test
    fun `test writing flattened map preserves hierarchy`() {
        val tempDir = Files.createTempDirectory("craftan-write-test").toFile()
        val configFile = File(tempDir, "write.yml")
        
        val config = FlattenedMapConfig(
            locale = "fr_FR",
            messages = mapOf(
                "lobby.welcome" to "Bienvenue",
                "cmd.help" to "Aide"
            )
        )
        
        val configHandle = Configs.of(FlattenedMapConfig::class, configFile, config)
        configHandle.get() // This should trigger a write if file doesn't exist
        
        val yaml = YamlConfiguration.loadConfiguration(configFile)
        assertEquals("fr_FR", yaml.getString("locale"))
        assertEquals("Bienvenue", yaml.getString("lobby.welcome"))
        assertEquals("Aide", yaml.getString("cmd.help"))
        
        // Check if it's actually nested in YAML structure
        val lobbySection = yaml.getConfigurationSection("lobby")
        val cmdSection = yaml.getConfigurationSection("cmd")
        
        assert(lobbySection != null) { "Lobby section should exist" }
        assert(cmdSection != null) { "Cmd section should exist" }
        assertEquals("Bienvenue", lobbySection?.getString("welcome"))
        assertEquals("Aide", cmdSection?.getString("help"))
    }

    @Test
    fun `test writing flattened map with default messages`() {
        val tempDir = Files.createTempDirectory("craftan-default-test").toFile()
        val configFile = File(tempDir, "default.yml")
        
        val defaultMessages = mapOf(
            "lobby.player.joined" to "%bc%The player %player% just joined the game!",
            "cmd.craftan.info.subcommand_prefix" to ">"
        )
        
        val defaults = LanguageFile(locale = "en_US", messages = defaultMessages)
        val configHandle = Configs.of(LanguageFile::class, configFile, defaults)
        val config = configHandle.get()
        
        assertEquals("en_US", config.locale)
        assertEquals(defaultMessages["lobby.player.joined"], config.messages["lobby.player.joined"])
        
        val yaml = YamlConfiguration.loadConfiguration(configFile)
        assertEquals("en_US", yaml.getString("locale"))
        assert(yaml.getConfigurationSection("lobby") != null) { "Lobby section should exist" }
        assertEquals(defaultMessages["lobby.player.joined"], yaml.getString("lobby.player.joined"))
    }

    @Test
    fun `test merging default messages with partial file`() {
        val tempDir = Files.createTempDirectory("craftan-merge-test").toFile()
        val configFile = File(tempDir, "merge.yml")
        
        val yaml = YamlConfiguration()
        yaml.set("locale", "en_US")
        yaml.set("prefix", "Custom Prefix")
        // Missing lobby.player.joined
        yaml.save(configFile)
        
        val defaultMessages = mapOf(
            "prefix" to "Default Prefix",
            "lobby.player.joined" to "Default Joined"
        )
        
        val defaults = LanguageFile(locale = "en_US", messages = defaultMessages)
        val configHandle = Configs.of(LanguageFile::class, configFile, defaults)
        val config = configHandle.get()
        
        assertEquals("Custom Prefix", config.messages["prefix"])
        assertEquals("Default Joined", config.messages["lobby.player.joined"])
    }
}
