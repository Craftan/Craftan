package de.craftan.io.config.validators

import org.bukkit.Material

fun validateResource(value: String) = Material.getMaterial(value) != null