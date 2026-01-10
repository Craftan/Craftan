package de.craftan.io.config

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object ConfigSchemaBuilder {

    fun buildSchema(clazz: KClass<*>, metadataQueue: Deque<PropertyMetadata>, instance: Any? = null): ConfigSchema {
        val constructor = clazz.primaryConstructor ?: error("No primary constructor for ${clazz.simpleName}")
        val properties = constructor.parameters.map { param ->
            val name = param.name ?: ""
            val type = param.type.classifier as KClass<*>
            
            val nestedInstance = if (instance != null) {
                val prop = clazz.members.firstOrNull { it.name == name }
                try { prop?.call(instance) } catch (_: Exception) { null }
            } else null

            val nestedSchema = if (isDataClass(type)) {
                buildSchema(type, metadataQueue, nestedInstance)
            } else null

            var metadata = if (param.isOptional) metadataQueue.pollFirst() else null
            if (metadata != null && nestedInstance != null) {
                metadata = metadata.copy(value = nestedInstance)
            }

            PropertySchema(name, type, param.type, metadata, nestedSchema)
        }
        return ConfigSchema(clazz, properties)
    }

    fun discoverMetadata(clazz: KClass<*>): Deque<PropertyMetadata> {
        ConfigRegistry.drain()
        val constructor = clazz.primaryConstructor ?: return LinkedList()
        try {
            constructor.callBy(emptyMap())
        } catch (_: Exception) {}
        return ConfigRegistry.drain()
    }

    fun isDataClass(clazz: KClass<*>): Boolean {
        return clazz.isData && !isPrimitive(clazz) && !isMap(clazz) && !isList(clazz)
    }

    fun isPrimitive(clazz: KClass<*>): Boolean {
        return clazz == String::class || clazz == Int::class || clazz == Long::class || 
               clazz == Double::class || clazz == Boolean::class || clazz.java.isEnum
    }

    fun isMap(clazz: KClass<*>): Boolean {
        return Map::class.java.isAssignableFrom(clazz.java)
    }

    fun isList(clazz: KClass<*>): Boolean {
        return List::class.java.isAssignableFrom(clazz.java)
    }
}
