package de.craftan.bridge

import de.craftan.engine.CraftanGame
import de.craftan.engine.CraftanGameConfig
import de.craftan.engine.CraftanGameImpl
import de.craftan.engine.gameflow.GameFlow

object CraftanGameProvider {

    fun createCraftanGame(config: CraftanGameConfig): CraftanGame {
        val gameFlow: GameFlow = TODO()
        return CraftanGameImpl(config, gameFlow)
    }

}