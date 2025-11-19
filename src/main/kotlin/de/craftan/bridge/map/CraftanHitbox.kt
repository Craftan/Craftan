package de.craftan.bridge.map

import de.craftan.Craftan
import de.craftan.Craftan.json
import de.craftan.InternalMain
import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.commands.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.Shulker
import org.bukkit.persistence.PersistentDataType
import java.util.*

class CraftanHitbox(val lobby: CraftanLobby, val location: Location) {

    val id: UUID = UUID.randomUUID()
    private val hitBox = location.world.spawnEntity(location, EntityType.INTERACTION) as Interaction

    companion object {
        val hitBoxNameSpace = NamespacedKey(InternalMain.INSTANCE, "craftan_hitbox_data")
    }

    init {
        hitBox.persistentDataContainer.set(hitBoxNameSpace, PersistentDataType.STRING, json.encodeToString(toData()))
        spawnHitbox()
    }

    private fun toData() = CraftanHitboxData(id, lobby.id)

    fun glow() {
        hitBox.isGlowing = true
    }

    fun unglow() {
        hitBox.isGlowing = false
    }

    private fun spawnHitbox() {
        val world = location.world

        Craftan.logger.fine("Spawning hitbox at $location")

        hitBox.setNoPhysics(true)
        hitBox.setGravity(false)
        hitBox.isGlowing = true

        hitBox.interactionWidth = 3.0f
        hitBox.interactionHeight = 1.0f

        for (dx in -1..1) {
            for (dz in -1..1) {
                val spawnLoc = Location(world, location.x + dx, location.y, location.z + dz)
                val entity = world.spawnEntity(spawnLoc, EntityType.SHULKER) as Shulker

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