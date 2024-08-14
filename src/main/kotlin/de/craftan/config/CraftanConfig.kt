package de.craftan.config

import com.uchuhimo.konf.ConfigSpec

object CraftanConfig : ConfigSpec("craftan") {
    val singleLobby by optional<Boolean>(
        true,
        description = "Whether craftan should handle this server as a single lobby, or allow multiple lobbies per this server.",
    )
}
