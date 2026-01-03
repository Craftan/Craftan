package de.craftan.io.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

data class InnerConfig(
    val value: Int = default(10)
)

data class OuterConfig(
    val inner: InnerConfig = default(InnerConfig()),
    val other: Int = default(20)
) : CraftanConfig("test.yml")

class NestedDataClassTest {
    @Test
    fun `test nested data class metadata discovery`() {
        val tempDir = Files.createTempDirectory("craftan-nested-test").toFile()
        val configFile = File(tempDir, "test.yml")
        
        // This should no longer throw ClassCastException
        val configHandle = Configs.of(OuterConfig::class, configFile)
        val config = configHandle.get()
        
        assertEquals(10, config.inner.value)
        assertEquals(20, config.other)
    }
}
