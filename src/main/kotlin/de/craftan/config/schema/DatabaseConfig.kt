package de.craftan.config.schema

import de.craftan.io.config.ConfigPath
import de.craftan.io.config.CraftanFileConfig
import de.craftan.io.config.annotations.Description
import de.craftan.io.config.annotations.Location

@ConfigPath("config/database.yml")
data class DatabaseConfig(
    @Description("The database type to use. (H2, or MYSQL)")
    @Location("")
    val database: DatabaseType = DatabaseType.H2,

    @Location("database.connection")
    val host: String = "localhost",

    @Location("database.connection")
    val port: Int = 3306,

    @Location("database.connection")
    val databaseName: String = "craftan",

    @Location("database.connection")
    val username: String = "craftan",

    @Location("database.connection")
    val password: String = "",
) : CraftanFileConfig

enum class DatabaseType {
    H2,
    MYSQL,
}
