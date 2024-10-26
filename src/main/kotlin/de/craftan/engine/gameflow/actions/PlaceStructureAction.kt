package de.craftan.engine.gameflow.actions

import de.craftan.CraftanGameAction
import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.CraftanActionItem
import de.craftan.engine.gameflow.craftanActionItem
import org.bukkit.Material

class PlaceStructureAction(
    override val game: CraftanGame,
) : CraftanGameAction<Void> {
    override var result: Void? = null

    override fun invoke(player: CraftanPlayer): Boolean {
        TODO()
    }

    override fun asItem(): CraftanActionItem<Void> = craftanActionItem(Material.WHITE_CONCRETE, 1, TODO(), this)
}
