package de.craftan.io.config

import de.craftan.config.schema.LanguageFile
import java.io.File

/**
 * Concrete adapter for the Craftan language YAML files.
 */
class LanguageYamlAdapter(
    file: File,
    defaultLayout: LanguageFile,
    createBackup: Boolean = true,
) : YamlConfigAdapter<LanguageFile>(file, defaultLayout, createBackup)
