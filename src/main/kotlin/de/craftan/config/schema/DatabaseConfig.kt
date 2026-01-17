package de.craftan.config.schema

import de.craftan.io.config.*
import de.craftan.io.config.CraftanConfig as ConfigBase

data class DatabaseConnectionConfig(
    val host: String = default("localhost", comment = "The database host"),
    val port: Int = inRange(1..65535).default(3306),
    val databaseName: String = default("craftan"),
    val username: String = default("craftan"),
    val password: String = default("")
)

enum class DatabaseType {
    H2, MYSQL
}

data class DatabaseConfig(
    val database: DatabaseType = default(DatabaseType.H2, comment = "The database type to use. (H2, or MYSQL)"),
    val connection: DatabaseConnectionConfig = default(DatabaseConnectionConfig())
) : ConfigBase("config/database.yml")
