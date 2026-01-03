package de.craftan.io.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files
import kotlin.reflect.KClass

class YamlConfigAdapter<T : Any>(
    private val kClass: KClass<T>,
    private val file: File,
    private val defaultInstance: T? = null,
    private val createBackup: Boolean = true
) {
    private val reader = ConfigReader(file)

    fun loadAndUpdate(): T {
        // 1. Discover metadata by instantiating with defaults
        val metadataQueue = ConfigSchemaBuilder.discoverMetadata(kClass)
        val schema = ConfigSchemaBuilder.buildSchema(kClass, metadataQueue, defaultInstance)
        
        val existedBefore = file.exists()
        val yaml = if (existedBefore) YamlConfiguration.loadConfiguration(file) else YamlConfiguration()
        
        @Suppress("UNCHECKED_CAST")
        val instance = reader.readInstance(yaml, schema) as T
        
        val output = YamlConfiguration()
        ConfigWriter.writeInstance(output, instance, schema)
        
        val desiredString = output.saveToString()
        val currentString = yaml.saveToString()
        
        if (!existedBefore || desiredString != currentString) {
            if (createBackup && existedBefore) {
                val target = File(file.parentFile, file.name + ".backup")
                try {
                    Files.copy(file.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
                } catch (_: Exception) {}
            }
            file.parentFile?.mkdirs()
            output.save(file)
        }
        
        return instance
    }
}
