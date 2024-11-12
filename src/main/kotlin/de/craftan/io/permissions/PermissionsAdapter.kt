package de.craftan.io.permissions

import de.craftan.PluginManager
import de.craftan.io.FileAdapter
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * Util class to manage permissions.yml file
 */
object PermissionsAdapter : FileAdapter {
    private val location = File(PluginManager.dataFolder, "permissions.yml")
    private lateinit var configuration: YamlConfiguration

    private val categories =
        mapOf(
            "commands" to "cmd",
            "events" to "event",
            "notifications" to "ntf",
        )

    override fun load() {
        if (!location.exists()) {
            location.parentFile.mkdirs()
            location.createNewFile()
        }
        configuration = YamlConfiguration.loadConfiguration(location)
    }

    /**
     * Writes the given permission to the permissions.yml file
     * Based on the raw permission string, the permission will be filled into a set category, or misc, if no category was found
     * @see categories
     * @param craftanPermission the permission to write to
     */
    fun writePermissions(craftanPermission: CraftanPermission) {
        var permissionsWritten = false
        for ((category, key) in categories) {
            if (craftanPermission.permission.contains(key)) {
                configuration.set("$category.${craftanPermission.permission.replace(".", "-")}.description", craftanPermission.description)
                configuration.set("$category.${craftanPermission.permission.replace(".", "-")}.permission", craftanPermission.permission)
                permissionsWritten = true
            }
        }

        if (!permissionsWritten) {
            configuration.set("misc.${craftanPermission.permission.replace(".", "-")}.description", craftanPermission.description)
            configuration.set("misc.${craftanPermission.permission.replace(".", "-")}.permission", craftanPermission.permission)
        }
        configuration.save(location)
    }
}
