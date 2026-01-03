package de.craftan.io.config

import org.bukkit.configuration.ConfigurationSection
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

class ConfigReader(private val file: File) {

    fun readInstance(section: ConfigurationSection, schema: ConfigSchema, basePath: String = ""): Any {
        val constructor = schema.kClass.primaryConstructor!!
        val args = mutableMapOf<kotlin.reflect.KParameter, Any?>()
        
        val propertyNames = schema.properties.map { it.name }.toSet()
        
        for (propSchema in schema.properties) {
            val param = constructor.parameters.first { it.name == propSchema.name }
            val fullPath = if (basePath.isEmpty()) propSchema.name else "$basePath.${propSchema.name}"
            val rawValue = if (propSchema.metadata?.flatten == true) section else section.get(propSchema.name)
            
            val value = readValue(
                rawValue, 
                propSchema.kType, 
                propSchema.metadata, 
                propSchema.nestedSchema,
                if (propSchema.metadata?.flatten == true) propertyNames else emptySet(), 
                fullPath
            )
            
            if (value != null) {
                if (propSchema.metadata != null) {
                    ConfigValidator.validate(file, value, propSchema.metadata, fullPath)
                }
                args[param] = value
            }
        }
        return constructor.callBy(args)
    }

    private fun readValue(
        value: Any?, 
        kType: KType, 
        metadata: PropertyMetadata?, 
        nestedSchema: ConfigSchema? = null,
        excludedKeys: Set<String> = emptySet(), 
        fullPath: String = ""
    ): Any? {
        val clazz = kType.classifier as? KClass<*> ?: return value
        if (value == null && metadata?.flatten != true) return metadata?.value
        
        return when {
            ConfigSchemaBuilder.isPrimitive(clazz) -> coercePrimitive(value ?: return metadata?.value, clazz)
            ConfigSchemaBuilder.isDataClass(clazz) -> {
                val section = value as? ConfigurationSection ?: return metadata?.value
                val schema = nestedSchema ?: run {
                    val nestedMetadata = ConfigSchemaBuilder.discoverMetadata(clazz)
                    ConfigSchemaBuilder.buildSchema(clazz, nestedMetadata)
                }
                readInstance(section, schema, fullPath)
            }
            ConfigSchemaBuilder.isMap(clazz) -> {
                val section = value as? ConfigurationSection ?: return metadata?.value
                val valueType = kType.arguments[1].type ?: return value
                val result = mutableMapOf<String, Any?>()
                for (key in section.getKeys(false)) {
                    if (key in excludedKeys) continue
                    val itemPath = if (fullPath.isEmpty()) key else "$fullPath.$key"
                    result[key] = readValue(section.get(key), valueType, null, null, emptySet(), itemPath)
                }
                if (result.isEmpty() && metadata?.value is Map<*, *>) return metadata.value
                result
            }
            ConfigSchemaBuilder.isList(clazz) -> {
                val list = value as? List<*> ?: return metadata?.value
                val elementType = kType.arguments[0].type ?: return value
                list.mapIndexed { index, it -> 
                    val itemPath = "$fullPath[$index]"
                    readValue(it, elementType, null, null, emptySet(), itemPath) 
                }
            }
            else -> value
        }
    }

    private fun coercePrimitive(value: Any, target: KClass<*>): Any? {
        return when (target) {
            String::class -> value.toString()
            Int::class -> (value as? Number)?.toInt() ?: value.toString().toIntOrNull()
            Long::class -> (value as? Number)?.toLong() ?: value.toString().toLongOrNull()
            Double::class -> (value as? Number)?.toDouble() ?: value.toString().toDoubleOrNull()
            Boolean::class -> value as? Boolean ?: value.toString().toBoolean()
            else -> {
                if (target.java.isEnum) {
                    val name = value.toString()
                    target.java.enumConstants.firstOrNull { (it as Enum<*>).name == name }
                } else value
            }
        }
    }
}
