package de.craftan.io.permissions

object CraftanPermissionRegistry {
    private val registeredPermissions = mutableSetOf<CraftanPermission>()

    fun registerPermission(permission: CraftanPermission) {
        registeredPermissions += permission
        PermissionsAdapter.writePermissions(permission)
    }
}
