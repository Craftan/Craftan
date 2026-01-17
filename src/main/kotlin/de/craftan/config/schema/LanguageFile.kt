package de.craftan.config.schema

import de.craftan.io.config.CraftanConfig
import de.craftan.io.config.default
import de.craftan.io.config.flatten

data class LanguageFile(
    val locale: String = default("en_US"),
    val messages: Map<String, String> = flatten<Map<String, String>>().default(emptyMap())
) : CraftanConfig("")