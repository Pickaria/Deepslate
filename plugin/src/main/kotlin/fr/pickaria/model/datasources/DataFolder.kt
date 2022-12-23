package fr.pickaria.model.datasources

import fr.pickaria.Main
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream

private val plugin = JavaPlugin.getProvidingPlugin(Main::class.java)

/**
 * Used to retrieve a resource file.
 * Creates the file if it doesn't exist.
 * @param filename The name of the resource file.
 * @return The input stream of the resource file.
 */
fun getResourceFileStream(filename: String): FileInputStream {
	val customConfigFile = File(plugin.dataFolder, filename)
	if (!customConfigFile.exists()) {
		customConfigFile.parentFile.mkdirs()
		plugin.saveResource(filename, false)
	}

	return customConfigFile.inputStream()
}