package de.craftan.io.config.annotations

/**
 * Marks a String/List<String>/Array<String> property as a Minecraft/Paper resource reference that must exist.
 * Currently supports MATERIAL resources (block/item types). Values can be enum-like (e.g., RED_WOOL)
 * or namespaced (e.g., minecraft:red_wool or mypack:custom_block).
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Resource(val kind: Kind = Kind.MATERIAL) {
    enum class Kind { MATERIAL }
}
