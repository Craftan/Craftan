package de.craftan.io.permissions

abstract class CraftanPermission(
    val permission: String,
) {
    fun buildPermission(): String = "craftan.$permission"
}
