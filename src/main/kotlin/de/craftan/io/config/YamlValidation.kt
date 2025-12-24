package de.craftan.io.config

import de.craftan.io.config.validation.ConfigValidator
import de.craftan.io.config.validation.LengthValidator
import de.craftan.io.config.validation.NumericRangeValidator
import de.craftan.io.config.validation.ResourceValidator
import de.craftan.io.config.validation.ValidationContext
import java.io.File
import kotlin.reflect.KProperty1

/**
 * Validation coordinator using a simple Pipe-and-Filters pipeline.
 *
 * The pipeline is an ordered list of validators. Each validator acts as a filter that:
 * - receives the current value and context,
 * - may throw to signal a validation failure,
 * - may return the (possibly transformed) value to pass downstream. Most validators return the input value unchanged.
 *
 * To extend validation, add your validator to [validatorPipeline] in the desired order.
 */
private val validatorPipeline: List<ConfigValidator> = listOf(
    NumericRangeValidator,
    LengthValidator,
    ResourceValidator,
)

internal fun validateProperty(
    property: KProperty1<out Any, *>,
    value: Any?,
    defaultValue: Any?,
    yamlPath: String,
    sourceFile: File,
    ownerName: String,
): Any? {
    val context = ValidationContext(sourceFile, yamlPath, ownerName, property.name)
    var currentValue = value
    for (validator in validatorPipeline) {
        currentValue = validator.validate(property, currentValue, defaultValue, context)
    }
    return currentValue
}
