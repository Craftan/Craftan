package de.craftan.io.config

import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass

object ConfigWriter {

    fun writeInstance(section: ConfigurationSection, instance: Any, schema: ConfigSchema) {
        for (propSchema in schema.properties) {
            val prop = schema.kClass.members.firstOrNull { it.name == propSchema.name } ?: continue
            val value = prop.call(instance) ?: continue
            
            writeValue(section, propSchema.name, value, propSchema.metadata?.flatten ?: false)
            
            val comments = buildComments(propSchema)
            if (comments.isNotEmpty()) {
                try {
                    section.setComments(propSchema.name, comments)
                } catch (_: Throwable) {}
            }
        }
    }

    private fun buildComments(propSchema: PropertySchema): List<String> {
        val comments = mutableListOf<String>()
        val metadata = propSchema.metadata
        
        // 1. User comment
        if (metadata?.comment != null) {
            comments.add(metadata.comment)
        }
        
        // 2. Expected type
        val typeName = propSchema.type.simpleName ?: "Any"
        comments.add("Type: $typeName")
        
        // 3. Boundaries
        metadata?.range?.let {
            comments.add("Range: $it")
        }
        metadata?.length?.let {
            comments.add("Min length: $it")
        }
        
        return comments
    }

    private fun writeValue(section: ConfigurationSection, path: String, value: Any, flatten: Boolean = false) {
        val clazz = value::class
        when {
            ConfigSchemaBuilder.isDataClass(clazz) -> {
                val subSection = section.createSection(path)
                val metadata = ConfigSchemaBuilder.discoverMetadata(clazz)
                val schema = ConfigSchemaBuilder.buildSchema(clazz, metadata)
                writeInstance(subSection, value, schema)
            }
            value is Map<*, *> -> {
                val targetSection = if (flatten) section else section.createSection(path)
                value.forEach { (k, v) -> if (v != null) writeValue(targetSection, k.toString(), v) }
            }
            value is List<*> -> {
                section.set(path, value.map { if (it is Enum<*>) it.name else it })
            }
            value is Enum<*> -> section.set(path, value.name)
            else -> section.set(path, value)
        }
    }
}
