package de.craftan.io.config

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

data class CommentedConfig(
    val port: Int = inRange(1..65535).comment("The database port").default(3306),
    val username: String = length(3).default("admin")
) : CraftanConfig("comment_test.yml")

class CommentTest {

    @Test
    fun `test generated comments`() {
        val tempDir = Files.createTempDirectory("craftan-comment-test").toFile()
        val configFile = File(tempDir, "comment_test.yml")
        
        val configHandle = Configs.of(CommentedConfig::class, configFile)
        configHandle.get() // This should write the default file with comments
        
        val content = configFile.readText()
        
        // Port comments
        assertTrue(content.contains("# The database port"), "Should contain user comment")
        assertTrue(content.contains("# Type: Int"), "Should contain type info")
        assertTrue(content.contains("# Range: 1..65535"), "Should contain range info")
        
        // Username comments
        assertTrue(content.contains("# Type: String"), "Should contain type info for username")
        assertTrue(content.contains("# Min length: 3"), "Should contain min length info")
    }
}
