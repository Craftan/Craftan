package de.craftan.engine.gameflow.actions

import de.craftan.engine.CraftanActionData
import de.craftan.engine.CraftanGameAction
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.CraftanActionItem
import de.craftan.engine.gameflow.craftanActionItem
import org.bukkit.Material

class PlaceStructureAction(
    override val game: CraftanGame,
) : CraftanGameAction<Unit> {
    override var result: Unit? = null

    override fun <T: CraftanActionData> invoke(player: CraftanPlayer, data: T): Boolean {
        TODO()
    }

    override fun asItem(): CraftanActionItem<Unit> = craftanActionItem(Material.WHITE_CONCRETE, 1, TODO(), this)
}
