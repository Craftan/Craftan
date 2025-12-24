package de.craftan.io.config.validation

import java.io.File

/**
 * Carries contextual information for validation error reporting.
 */
data class ValidationContext(
    val sourceFile: File,
    val yamlPath: String,
    val ownerName: String,
    val propName: String,
)
