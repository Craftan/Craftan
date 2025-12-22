package de.craftan.io.config.sample

import java.io.File

/**
 * Utility to write/update a sample YAML config demonstrating the abstract YAML adapter.
 */
object SampleConfigFiles {
    /**
     * Writes/updates the sample config file under dataFolder/config/sample.yml.
     * Creates a timestamped backup if a file already exists.
     */
    fun write(dataFolder: File) {
        val file = File(dataFolder, "config/sample.yml")
        file.parentFile.mkdirs()
        SampleConfigAdapter(file).loadAndUpdate()
    }
}
