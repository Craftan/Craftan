package de.craftan.io.config.sample

import de.craftan.io.config.YamlConfigAdapter
import java.io.File

/**
 * Concrete adapter for the example SampleConfig schema.
 */
class SampleConfigAdapter(
    file: File,
    defaultLayout: SampleConfig = SampleConfig(),
    createBackup: Boolean = true,
) : YamlConfigAdapter<SampleConfig>(file, defaultLayout, createBackup)
