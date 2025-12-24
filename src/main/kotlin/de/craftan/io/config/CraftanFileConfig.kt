package de.craftan.io.config

/**
 * Inline documentation for Craftan's configuration system. This file is meant to be discoverable in IDEs
 * and provide examples without relying on external markdown.
 *
 * Overview
 * - Data classes that represent YAML configs must implement CraftanFileConfig.
 * - Use @ConfigPath on the class to define the default relative location under the plugin data folder.
 * - Use @Location on properties to group related keys under a parent path; the property name is appended.
 * - Use @MapKey (or @Map) to emit a property under a specific key in a YAML map (e.g., colors.red).
 * - Use @Description on properties to write a human-friendly comment beside the YAML key.
 * - Use validation annotations to enforce constraints: @Min, @Max, @Length, and @Resource.
 * - Configs.get<T>() and ConfigFile.get() ALWAYS read/merge the file from disk (live values). Requires opt-in: @OptIn(LiveConfigRead::class).
 *
 * Example
 * @ConfigPath("config/craftan.yml")
 * data class CraftanConfig(
 *   val singleLobby: Boolean = false,
 *   @Location("game.defaults")
 *   @Description("Default game-related values")
 *   @Min(1.0) @Max(3600.0)
 *   val turnDuration: Int = 60,
 *
 *   // Produces:
 *   // colors:\n  *   red:\n  *     - 0xFF0000\n  *     - RED_WOOL
 *   @Location("colors")
 *   @MapKey("red")
 *   @Resource // validates that entries like RED_WOOL are valid Materials; hex like 0xFF0000 is allowed
 *   val red: List<String> = listOf("0xFF0000", "RED_WOOL"),
 * ) : CraftanFileConfig
 *
 * Loading (live provider)
 * object ConfigSystem : CraftanSystem {
 *   @OptIn(LiveConfigRead::class)
 *   override fun load() {
 *     val handle = Configs.of<CraftanConfig>()
 *     Craftan.config = handle::get // each invocation reads from disk and merges, updating the file if needed
 *     handle.get() // initial read to create/update the file
 *   }
 * }
 *
 * Validation behavior
 * - If a value in YAML violates declared constraints, the adapter throws ConfigValidationException with a clear
 *   message, including the file path, YAML key and property name. This is fail-fast to stop plugin boot.
 *
 * Notes
 * - For lists and arrays, @Length applies to the number of items.
 * - For strings, @Length applies to character count.
 * - @FlattenToRoot on Map<String, V> writes map entries directly under the current prefix/root.
 */
interface CraftanFileConfig
