package de.craftan.io.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

data class NestedConfig(
    val value: Int = inRange(1..10).default(5)
)

data class TestValidationConfig(
    val name: String = notEmpty().default("Test"),
    val nested: NestedConfig = default(NestedConfig())
) : CraftanConfig("test.yml")

data class ManualConfig(
    val value: Int = validate<Int>("Must be even") { it % 2 == 0 }.default(0)
) : CraftanConfig("test.yml")

class ValidationTest {

    @Test
    fun `test validation failure with path and line`() {
        val tempDir = Files.createTempDirectory("craftan-test").toFile()
        val configFile = File(tempDir, "test.yml")
        configFile.writeText("""
name: ""
nested:
  value: 15
""".trimIndent())

        val configHandle = Configs.of(TestValidationConfig::class, configFile)

        val exception = assertThrows(ConfigValidationException::class.java) {
            configHandle.get()
        }

        println("Exception message: ${exception.message}")
        assertEquals("name", exception.path)
        assertEquals(1, exception.line)
    }

    @Test
    fun `test nested validation failure`() {
        val tempDir = Files.createTempDirectory("craftan-test").toFile()
        val configFile = File(tempDir, "test.yml")
        configFile.writeText("""
name: "Valid"
nested:
  value: 15
""".trimIndent())

        val configHandle = Configs.of(TestValidationConfig::class, configFile)

        val exception = assertThrows(ConfigValidationException::class.java) {
            configHandle.get()
        }

        println("Exception message: ${exception.message}")
        assertEquals("nested.value", exception.path)
        assertEquals(3, exception.line)
    }

    @Test
    fun `test custom validation message`() {
        val tempDir = Files.createTempDirectory("craftan-test").toFile()
        val configFile = File(tempDir, "test.yml")
        configFile.writeText("value: 13")

        val configHandle = Configs.of(ManualConfig::class, configFile)
        val exception = assertThrows(ConfigValidationException::class.java) {
            configHandle.get()
        }
        assertEquals("Must be even", exception.error)
    }
}
