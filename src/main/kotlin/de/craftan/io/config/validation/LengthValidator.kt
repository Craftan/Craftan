package de.craftan.io.config.validation

import de.craftan.io.config.annotations.Length
import de.craftan.io.config.exceptions.ConfigValidationException
import kotlin.math.max
import kotlin.reflect.KProperty1

/**
 * Validates String/List/Array sizes against @Length.
 */
object LengthValidator : ConfigValidator {
    override fun validate(
        prop: KProperty1<out Any, *>,
        value: Any?,
        defaultValue: Any?,
        context: ValidationContext
    ): Any? {
        if (value == null) return defaultValue
        val lengthAnnotation = prop.annotations.filterIsInstance<Length>().firstOrNull() ?: return value
        val minLength = max(0, lengthAnnotation.min)
        val maxLength = max(lengthAnnotation.max, minLength)
        when (value) {
            is String -> checkLength(value.length, minLength, maxLength, context)
            is List<*> -> checkLength(value.size, minLength, maxLength, context)
            is Array<*> -> checkLength(value.size, minLength, maxLength, context)
        }
        return value
    }

    private fun checkLength(length: Int, minLength: Int, maxLength: Int, context: ValidationContext) {
        if (length < minLength) {
            throw ConfigValidationException(
                context.sourceFile.absolutePath, context.yamlPath, context.ownerName, context.propName,
                "Length $length is less than min $minLength"
            )
        }
        if (length > maxLength) {
            throw ConfigValidationException(
                context.sourceFile.absolutePath, context.yamlPath, context.ownerName, context.propName,
                "Length $length exceeds max $maxLength"
            )
        }
    }
}
