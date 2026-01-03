package de.craftan.config

import de.craftan.io.config.*
import de.craftan.io.config.CraftanConfig as ConfigBase

data class LanguageFile(
    val locale: String = default("en_US"),
    val messages: Map<String, String> = flatten<Map<String, String>>().default(emptyMap())
) : ConfigBase("")
