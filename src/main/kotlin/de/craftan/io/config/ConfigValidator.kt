package de.craftan.io.config

import java.io.File

object ConfigValidator {

    fun validate(file: File, value: Any, metadata: PropertyMetadata, fullPath: String) {
        metadata.range?.let {
            if (value is Int && value !in it) {
                throw ConfigValidationException(file, fullPath, "Value $value is out of range $it", findLineNumber(file, fullPath))
            }
        }
        metadata.length?.let {
            if (value is String && value.length < it) {
                throw ConfigValidationException(file, fullPath, "Value is too short (min $it, actual ${value.length})", findLineNumber(file, fullPath))
            }
        }
        metadata.validators.forEach { validator ->
            val error = validator(value)
            if (error != null) {
                throw ConfigValidationException(file, fullPath, error, findLineNumber(file, fullPath))
            }
        }
    }

    fun findLineNumber(file: File, path: String): Int {
        if (!file.exists()) return -1
        return try {
            val lines = file.readLines()
            val parts = path.split('.')
            var currentLine = 0
            var lastIndent = -1
            
            for (part in parts) {
                val key = part.substringBefore('[')
                val regex = Regex("^\\s*${Regex.escape(key)}\\s*:")
                var found = false
                for (i in currentLine until lines.size) {
                    val line = lines[i]
                    if (line.trim().startsWith("#")) continue
                    
                    val match = regex.find(line)
                    if (match != null) {
                        val indent = line.takeWhile { it.isWhitespace() }.length
                        if (indent > lastIndent) {
                            lastIndent = indent
                            currentLine = i
                            found = true
                            break
                        }
                    }
                }
                if (!found) return -1
            }
            currentLine + 1
        } catch (_: Exception) {
            -1
        }
    }
}
