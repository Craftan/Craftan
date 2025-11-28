package de.craftan.bridge.items.behaviors

import de.staticred.kia.KIA
import de.staticred.kia.behaviour.builder.kItemBehavior
import de.staticred.kia.util.KIdentifier

object DiceBehavior {
    val behavior = kItemBehavior(KIdentifier(KIA.plugin, "dice")) {
        if (!it.action.isRightClick) return@kItemBehavior
        val player = it.player
        val rolled = (1..6).random()
        player.sendMessage("You rolled a $rolled!")
    }
}