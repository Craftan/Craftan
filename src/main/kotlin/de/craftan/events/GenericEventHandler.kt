package de.craftan.events

abstract class GenericEventHandler<T, Param> {
    private val eventMapping = mutableMapOf<String, T.(p: Param?) -> Unit>()

    fun <P : Param> registerEvent(
        key: String,
        block: T.(p: P?) -> Unit,
    ) {
        eventMapping[key] = block
    }

    protected fun triggerEvent(
        key: String,
        eventObject: T,
        eventParameters: P? = null,
    ) {
        eventMapping[key]?.invoke(eventObject, eventParameters)
    }
}

open class EventParameters
