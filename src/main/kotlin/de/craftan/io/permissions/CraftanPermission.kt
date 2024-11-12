package de.craftan.io.permissions

/**
 * Models a permission for craftan
 *
 * Will be registered in permissions.yml
 *
 * @param permission the raw permission string
 * @param description of the permission
 */
data class CraftanPermission(
    val permission: String,
    val description: String,
) {
    init {
        CraftanPermissionRegistry.registerPermission(this)
    }
}
