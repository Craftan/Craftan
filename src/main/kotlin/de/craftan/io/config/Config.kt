package de.craftan.io.config

import de.craftan.PluginManager
import java.io.File
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

/**
 * Metadata captured during the execution of default value expressions.
 */
data class PropertyMetadata(
    val value: Any?,
    val range: IntRange? = null,
    val length: Int? = null,
    val comment: String? = null,
    val validators: List<(Any?) -> String?> = emptyList(),
    val flatten: Boolean = false
)

class ConfigValidationException(
    val file: File,
    val path: String,
    val error: String,
    val line: Int = -1
) : RuntimeException(
    "Validation failed in ${file.name}${if (line > 0) " at line $line" else ""} (path: '$path'): $error"
)

/**
 * Thread-local registry to capture metadata.
 */
object ConfigRegistry {
    private val queue = ThreadLocal.withInitial { LinkedList<PropertyMetadata>() as Deque<PropertyMetadata> }

    fun register(metadata: PropertyMetadata) {
        queue.get().addLast(metadata)
    }

    fun poll(): PropertyMetadata? = queue.get().pollFirst()
    
    fun drain(): Deque<PropertyMetadata> {
        val q = queue.get()
        val result = LinkedList(q)
        q.clear()
        return result
    }
}

/**
 * The new base class for configurations.
 */
abstract class CraftanConfig(val path: String) : CraftanFileConfig

interface CraftanFileConfig

/**
 * DSL Builder for properties.
 */
class PropertyBuilder<T>(
    private val range: IntRange? = null,
    private val length: Int? = null,
    private var comment: String? = null,
    private val validators: MutableList<(Any?) -> String?> = mutableListOf(),
    private var flatten: Boolean = false
) {
    fun comment(text: String) = apply { this.comment = text }
    
    fun flatten() = apply { this.flatten = true }
    
    fun validate(block: (T) -> Boolean) = validate("Validation failed", block)

    fun validate(message: String, block: (T) -> Boolean) = apply { 
        validators.add { 
            @Suppress("UNCHECKED_CAST")
            if (block(it as T)) null else message 
        } 
    }
    
    fun validateWith(block: (T) -> String?) = apply {
        validators.add {
            @Suppress("UNCHECKED_CAST")
            block(it as T)
        }
    }
    
    fun config(block: PropertyBuilder<T>.() -> Unit) = apply { this.block() }

    fun default(value: T): T {
        ConfigRegistry.register(PropertyMetadata(value, range, length, comment, validators.toList(), flatten))
        return value
    }
}

// Global DSL functions
fun inRange(range: IntRange) = PropertyBuilder<Int>(range = range)
fun min(min: Int) = PropertyBuilder<Int>(range = min..Int.MAX_VALUE)
fun max(max: Int) = PropertyBuilder<Int>(range = Int.MIN_VALUE..max)
fun `in`(range: IntRange) = inRange(range)
fun length(len: Int) = PropertyBuilder<String>(length = len)
fun <T> default(value: T) = PropertyBuilder<T>().default(value)
fun <T> default(value: T, comment: String) = PropertyBuilder<T>().comment(comment).default(value)
fun <T> flatten() = PropertyBuilder<T>().flatten()

fun <T> notNull() = PropertyBuilder<T>().validate("Must not be null") { it != null }
fun notEmpty() = PropertyBuilder<String>().validate("Must not be empty") { it.isNotEmpty() }
fun positive() = PropertyBuilder<Int>().validate("Must be positive") { it > 0 }

fun <T> validate(message: String, block: (T) -> Boolean) = PropertyBuilder<T>().validate(message, block)
fun <T> validateWith(block: (T) -> String?) = PropertyBuilder<T>().validateWith(block)

class ConfigSchema(
    val kClass: KClass<*>,
    val properties: List<PropertySchema>
)

class PropertySchema(
    val name: String,
    val type: KClass<*>,
    val kType: KType,
    val metadata: PropertyMetadata?,
    val nestedSchema: ConfigSchema? = null
)

/**
 * High-level handle for v2 configs.
 */
class ConfigFile<T : Any>(
    private val clazz: KClass<T>,
    private val file: File,
    private val defaultInstance: T? = null,
    private val createBackup: Boolean = true
) {
    private var cached: T? = null
    private val adapter by lazy { YamlConfigAdapter(clazz, file, defaultInstance, createBackup) }

    fun get(): T {
        val loaded = adapter.loadAndUpdate()
        cached = loaded
        return loaded
    }

    fun cachedOrNull(): T? = cached
}

object Configs {
    fun <T : CraftanConfig> of(clazz: KClass<T>, createBackup: Boolean = true): ConfigFile<T> {
        val ctor = clazz.primaryConstructor ?: error("No primary constructor for ${clazz.simpleName}")
        val instance = ctor.callBy(emptyMap())
        val file = File(PluginManager.dataFolder, instance.path)
        return ConfigFile(clazz, file, createBackup = createBackup)
    }

    fun <T : Any> of(clazz: KClass<T>, file: File, defaultInstance: T? = null, createBackup: Boolean = true): ConfigFile<T> {
        return ConfigFile(clazz, file, defaultInstance, createBackup)
    }
}
