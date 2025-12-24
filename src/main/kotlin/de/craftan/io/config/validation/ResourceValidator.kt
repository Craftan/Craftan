package de.craftan.io.config.validation

import de.craftan.io.config.annotations.Resource
import de.craftan.io.config.exceptions.ConfigValidationException
import org.bukkit.Material
import kotlin.reflect.KProperty1

/**
 * Validates values annotated with @Resource. Currently supports MATERIAL resources.
 * Resolution delegates to Bukkit/Paper's Material.matchMaterial, which understands
 * enum-like names (e.g., RED_WOOL) and namespaced keys (e.g., minecraft:red_wool).
 *
 * Supports TYPE-USE on generics, e.g.:
 * - List<@Resource String>
 * - Array<@Resource String>
 * - Map<String, @Resource String>
 */
object ResourceValidator : ConfigValidator {

    override fun validate(
        prop: KProperty1<out Any, *>,
        value: Any?,
        defaultValue: Any?,
        context: ValidationContext
    ): Any? {
        if (value == null) return defaultValue

        // Property-level annotation first
        val propertyAnnotation = prop.annotations.filterIsInstance<Resource>().firstOrNull()
        if (propertyAnnotation != null) {
            // Run validation side-effect (throws on failure) and keep original value
            validateValueWithAnnotation(value, propertyAnnotation, context)
            return value
        }

        // Then check TYPE-USE annotations on generic arguments
        val returnType = prop.returnType
        val genericArguments = returnType.arguments
        // Map<*, V>
        if (returnType.classifier == Map::class && genericArguments.size == 2) {
            val valueType = genericArguments[1].type
            val valueAnnotation = valueType?.annotations?.filterIsInstance<Resource>()?.firstOrNull()
            if (valueAnnotation != null && value is Map<*, *>) {
                value.forEach { (entryKey, entryValue) ->
                    val subContext = context.copy(yamlPath = context.yamlPath + "." + entryKey)
                    validateValueWithAnnotation(entryValue, valueAnnotation, subContext)
                }
                return value
            }
        }
        // List<E>
        if (returnType.classifier == List::class && genericArguments.size == 1) {
            val elementType = genericArguments[0].type
            val elementAnnotation = elementType?.annotations?.filterIsInstance<Resource>()?.firstOrNull()
            if (elementAnnotation != null && value is List<*>) {
                value.forEachIndexed { index, element ->
                    val subContext = context.copy(yamlPath = context.yamlPath + "[" + index + "]")
                    validateValueWithAnnotation(element, elementAnnotation, subContext)
                }
                return value
            }
        }
        // Array<E>
        val isArray = returnType.classifier == Array<Any>::class || (value is Array<*>)
        if (isArray && genericArguments.size == 1) {
            val elementType = genericArguments[0].type
            val elementAnnotation = elementType?.annotations?.filterIsInstance<Resource>()?.firstOrNull()
            if (elementAnnotation != null && value is Array<*>) {
                value.forEachIndexed { index, element ->
                    val subContext = context.copy(yamlPath = context.yamlPath + "[" + index + "]")
                    validateValueWithAnnotation(element, elementAnnotation, subContext)
                }
                return value
            }
        }

        return value
    }

    private fun validateValueWithAnnotation(value: Any?, annotation: Resource, context: ValidationContext) {
        when (value) {
            is String -> validateString(annotation, value, context)
            is List<*> -> value.forEachIndexed { index, element -> if (element is String) validateString(annotation, element, context.copy(yamlPath = context.yamlPath + "[" + index + "]")) }
            is Array<*> -> value.forEachIndexed { index, element -> if (element is String) validateString(annotation, element, context.copy(yamlPath = context.yamlPath + "[" + index + "]")) }
            is Map<*, *> -> value.forEach { (entryKey, entryValue) -> if (entryValue is String) validateString(annotation, entryValue, context.copy(yamlPath = context.yamlPath + "." + entryKey)) }
        }
    }

    private fun validateString(annotation: Resource, resourceString: String, context: ValidationContext) {
        when (annotation.kind) {
            Resource.Kind.MATERIAL -> if (!isValidMaterial(resourceString)) {
                throw ConfigValidationException(
                    context.sourceFile.absolutePath, context.yamlPath, context.ownerName, context.propName,
                    "Unknown MATERIAL resource '$resourceString'"
                )
            }
        }
    }

    private fun isValidMaterial(resourceString: String): Boolean {
        return Material.matchMaterial(resourceString) != null
    }
}
