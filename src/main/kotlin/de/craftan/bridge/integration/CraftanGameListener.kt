package de.craftan.bridge.integration

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.engine.gameflow.events.GameStartGameEvent

class CraftanGameListener(val lobby: CraftanLobby) {

    private val bus = lobby.game.eventBus

    init {
        registerListeners()
    }

    private fun registerListeners() {
       bus.on<GameStartGameEvent> {  }
    }

}