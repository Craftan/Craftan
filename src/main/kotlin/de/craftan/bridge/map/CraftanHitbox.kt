package de.craftan.bridge.map

import de.craftan.Craftan
import de.craftan.Craftan.json
import de.craftan.InternalMain
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.commands.UUIDSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.Shulker
import org.bukkit.persistence.PersistentDataType
import java.util.*

class CraftanHitbox(val lobby: CraftanLobby, val location: Location) {

    val id: UUID = UUID.randomUUID()
    val hitBoxEntity = location.world.spawnEntity(location, EntityType.INTERACTION) as Interaction
    val shulkerEntites = mutableListOf<Shulker>()

    companion object {
        val hitBoxNameSpace = NamespacedKey(InternalMain.INSTANCE, "craftan_hitbox_data")
    }

    init {
        hitBoxEntity.persistentDataContainer.set(hitBoxNameSpace, PersistentDataType.STRING, json.encodeToString(toData()))
        spawnHitbox()
    }

    private fun toData() = CraftanHitboxData(id, lobby.id)

    fun glow() {
        shulkerEntites.forEach { it.isGlowing = true }
    }

    fun unglow() {
        shulkerEntites.forEach { it.isGlowing = false }
    }

    private fun spawnHitbox() {
        val world = location.world

        Craftan.logger.fine("Spawning hitbox at $location")

        hitBoxEntity.setNoPhysics(true)
        hitBoxEntity.setGravity(false)

        hitBoxEntity.interactionWidth = 3.0f
        hitBoxEntity.interactionHeight = 1.0f

        for (dx in -1..1) {
            for (dz in -1..1) {
                val spawnLoc = Location(world, location.x + dx, location.y, location.z + dz)
                val entity = world.spawnEntity(spawnLoc, EntityType.SHULKER) as Shulker

                shulkerEntites.add(entity)

                entity.setGravity(false)
                entity.setNoPhysics(true)
                entity.setAI(false)
                entity.isInvisible = true
                entity.isInvulnerable = true
            }
        }
    }
}

@Serializable
data class CraftanHitboxData(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val lobbyID: Int
)