package de.craftan.config.schema

import de.craftan.io.config.CraftanFileConfig
import de.craftan.io.config.annotations.FlattenToRoot

/**
 * Data layout for a language YAML file.
 * The [messages] map is flattened to the root of the YAML using [de.craftan.io.config.annotations.FlattenToRoot],
 * so entries appear as top-level keys next to the [locale] field.
 */
data class LanguageFile(
    val locale: String,
    @FlattenToRoot val messages: Map<String, String>,
) : CraftanFileConfig