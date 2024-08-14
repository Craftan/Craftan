package de.craftan.util

object SystemManager {
    private val systems = mutableSetOf<CraftanSystem>()

    fun registerSystem(system: CraftanSystem) = systems.add(system)

    fun loadSystems() {
        systems.forEach { it.load() }
    }
}
