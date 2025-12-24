package de.craftan.io.config.validation

import kotlin.reflect.KProperty1

/**
 * Contract for a single config validator that can check a property value.
 * Validators should be pure and throw ConfigValidationException on failure.
 */
interface ConfigValidator {
    fun validate(prop: KProperty1<out Any, *>, value: Any?, defaultValue: Any?, context: ValidationContext): Any?
}
