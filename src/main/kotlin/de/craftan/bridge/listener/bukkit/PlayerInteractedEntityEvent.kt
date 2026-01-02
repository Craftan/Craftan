package de.craftan.bridge.listener.bukkit

import de.craftan.Craftan
import de.craftan.bridge.map.CraftanHitbox
import de.craftan.bridge.map.CraftanHitboxData
import net.axay.kspigot.event.listen
import org.bukkit.entity.Interaction
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

class PlayerInteractedEntityEvent: CraftanBukkitListener {

    override fun register() {
        listen<PlayerInteractEvent> {
            if (it.action != Action.LEFT_CLICK_AIR) return@listen

            val player = it.player

            val result = player.rayTraceEntities(120, true)
            result?.hitEntity?.let { entity ->
                if (entity !is Interaction) return@listen

                val dataRaw = entity.persistentDataContainer.get(
                    CraftanHitbox.Companion.hitBoxNameSpace,
                    PersistentDataType.STRING
                ) ?: return@listen
                val data = Craftan.json.decodeFromString<CraftanHitboxData>(dataRaw)

                player.sendMessage("Hit hitbox with the data: $data")
            }
        }
    }
}