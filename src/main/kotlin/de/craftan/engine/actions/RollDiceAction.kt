package de.craftan.engine.actions

import de.craftan.CraftanGameAction
import de.craftan.engine.*
import de.craftan.engine.events.actions.RolledDiceEvent
import de.craftan.engine.map.DiceNumber
import org.bukkit.Material
import kotlin.random.Random
import kotlin.random.nextInt

class RollDiceAction(
    override val game: CraftanGame,
) : CraftanGameAction<DiceNumber> {
    override var result: DiceNumber? = null

    override fun invoke(player: CraftanPlayer): Boolean {
        val dice1 = Random.nextInt(1..6)
        val dice2 = Random.nextInt(1..6)

        val diceResult = dice1 + dice2
        val diceNumber = DiceNumber.entries.first { it.value == diceResult }
        result = diceNumber

        eventBus.fire(RolledDiceEvent(game, diceNumber, player))

        return true
    }

    override fun asItem(): CraftanActionItem<DiceNumber> = craftanActionItem(Material.WHITE_CONCRETE, 1, TODO(), this)
}
