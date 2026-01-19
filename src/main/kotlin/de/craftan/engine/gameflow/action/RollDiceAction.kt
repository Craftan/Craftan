package de.craftan.engine.gameflow.action

import de.craftan.engine.CraftanGameStateHandler
import de.craftan.engine.CraftanPlayer
import de.craftan.engine.gameflow.GameFlow
import de.craftan.engine.map.DiceNumber


class RollDiceAction(): CraftanGameAction<RollDiceActionData, DiceNumber> {
    // Extract roll logic to a method that can be spied on
    open fun rollDice(): Int = (1..6).random() + (1..6).random()

    override fun invoke(player: CraftanPlayer, data: RollDiceActionData, stateHandler: CraftanGameStateHandler): DiceNumber {
        val rolledNumber = rollDice()
        println("Player ${player.name} rolled $rolledNumber")
        stateHandler.distributeResources(rolledNumber)
        return DiceNumber.entries.first { it.value == rolledNumber }
    }

    override fun verify(player: CraftanPlayer, data: RollDiceActionData, stateHandler: CraftanGameStateHandler, gameFlow: GameFlow): VerificationResult {
        return VerificationResult(true, null)
    }
}

class RollDiceActionData (): CraftanActionData