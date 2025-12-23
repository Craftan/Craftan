package de.craftan.io

import de.craftan.io.config.CraftanFileConfig
import de.craftan.io.config.FlattenToRoot

/**
 * Data layout for a language YAML file.
 * The [messages] map is flattened to the root of the YAML using [FlattenToRoot],
 * so entries appear as top-level keys next to the [locale] field.
 */
data class LanguageFile(
    val locale: String,
    @FlattenToRoot val messages: Map<String, String>,
) : CraftanFileConfig
