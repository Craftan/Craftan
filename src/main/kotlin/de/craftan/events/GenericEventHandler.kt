package de.craftan.events

/**
 *
 */
class GenericEventHandler<E, P : EventParameters> {
    private val eventMapping = mutableMapOf<String, E.(p: P?) -> Unit>()

    fun registerEvent(
        key: String,
        block: E.(p: P?) -> Unit,
    ) {
        eventMapping[key] = block
    }

    fun triggerEvent(
        key: String,
        eventObject: E,
        eventParameters: P? = null,
    ) {
        eventMapping[key]?.invoke(eventObject, eventParameters)
    }
}

open class EventParameters
