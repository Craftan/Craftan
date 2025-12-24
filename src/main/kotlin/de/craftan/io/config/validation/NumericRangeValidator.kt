package de.craftan.io.config.validation

import de.craftan.io.config.annotations.Max
import de.craftan.io.config.annotations.Min
import de.craftan.io.config.exceptions.ConfigValidationException
import kotlin.reflect.KProperty1

/**
 * Validates numeric values against @Min/@Max.
 */
object NumericRangeValidator : ConfigValidator {
    override fun validate(
        prop: KProperty1<out Any, *>,
        value: Any?,
        defaultValue: Any?,
        context: ValidationContext
    ): Any? {
        if (value == null) return defaultValue
        val minAnnotation = prop.annotations.filterIsInstance<Min>().firstOrNull()
        val maxAnnotation = prop.annotations.filterIsInstance<Max>().firstOrNull()
        if (minAnnotation == null && maxAnnotation == null) return value

        fun check(numericValue: Double) {
            val minValue = minAnnotation?.value
            val maxValue = maxAnnotation?.value
            if (minValue != null && numericValue < minValue) {
                throw ConfigValidationException(
                    context.sourceFile.absolutePath, context.yamlPath, context.ownerName, context.propName,
                    "Value $numericValue is less than min $minValue"
                )
            }
            if (maxValue != null && numericValue > maxValue) {
                throw ConfigValidationException(
                    context.sourceFile.absolutePath, context.yamlPath, context.ownerName, context.propName,
                    "Value $numericValue exceeds max $maxValue"
                )
            }
        }

        when (value) {
            is Int -> check(value.toDouble())
            is Long -> check(value.toDouble())
            is Double -> check(value)
        }
        return value
    }
}
