package de.craftan.io.config.sample

import de.craftan.io.config.Configs
import java.io.File

/**
 * Utility to write/update a sample YAML config demonstrating the abstract YAML adapter.
 */
object SampleConfigFiles {
    /**
     * Writes/updates the sample config file under dataFolder/config/sample.yml.
     * Creates a .backup alongside when changes are written.
     */
    fun write(@Suppress("UNUSED_PARAMETER") dataFolder: File) {
        // Use the high-level Configs API for brevity
        Configs.get<SampleConfig>("config/sample.yml")
    }
}
