package de.craftan.io

import de.craftan.Craftan
import java.util.logging.Logger

fun Logger.debug(message: Any) {
    if (Craftan.config().debug) {
        this.fine("[DEBUG] $message")
    }
}