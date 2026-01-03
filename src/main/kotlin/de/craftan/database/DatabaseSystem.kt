package de.craftan.database

import de.craftan.Craftan
import de.craftan.InternalMain
import de.craftan.config.schema.DatabaseType
import de.craftan.database.dao.PlayerStatsTable
import de.craftan.util.CraftanSystem
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseSystem : CraftanSystem {

    lateinit var database: Database
        private set

    override fun load() {
        val config = Craftan.configs.database()

        database = when (config.database) {
            DatabaseType.H2 -> {
                val file = File(InternalMain.INSTANCE.dataFolder, "database.h2")

                Database.connect(
                    url = "jdbc:h2:file:${file.absolutePath};AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=MySQL",
                    driver = "org.h2.Driver",
                    user = "sa",
                    password = ""
                )
            }
            DatabaseType.MYSQL -> {
                Database.connect(
                    url = "jdbc:mysql://${config.host}:${config.port}/${config.databaseName}",
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = config.username,
                    password = config.password
                )
            }
        }

        Craftan.logger.info("[DatabaseHandler] Connected to ${config.database} database.")
        
        transaction(database) {
            SchemaUtils.create(PlayerStatsTable)
        }
    }
}
