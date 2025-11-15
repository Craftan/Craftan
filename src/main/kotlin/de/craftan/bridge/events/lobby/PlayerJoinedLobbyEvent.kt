package de.craftan.bridge.events.lobby

import de.craftan.bridge.lobby.CraftanLobby
import de.craftan.io.CancellableCraftanEvent
import org.bukkit.entity.Player

class PlayerJoinedLobbyEvent(val lobby: CraftanLobby, player: Player): CancellableCraftanEvent() {


}