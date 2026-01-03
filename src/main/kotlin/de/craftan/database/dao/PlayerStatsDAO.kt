package de.craftan.database.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import java.util.*

object PlayerStatsTable : UUIDTable("craftan_player_stats") {
    val gamesPlayed = integer("games_played").default(0)
    val gamesWon = integer("games_won").default(0)
}

class PlayerStatsDAO(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerStatsDAO>(PlayerStatsTable)

    var gamesPlayed by PlayerStatsTable.gamesPlayed
    var gamesWon by PlayerStatsTable.gamesWon
}