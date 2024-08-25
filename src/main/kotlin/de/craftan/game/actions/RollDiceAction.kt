package de.craftan.game.actions

import de.craftan.game.*
import de.craftan.game.map.DiceNumber
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
        result = DiceNumber.entries.first { it.value == diceResult }
        return true
    }

    override fun asItem(): CraftanActionItem<DiceNumber> = craftanActionItem(Material.WHITE_CONCRETE, 1, TODO(), this)
}
