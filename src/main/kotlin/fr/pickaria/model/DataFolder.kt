package fr.pickaria.model

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.plugin
import java.io.File
import java.io.FileInputStream

/**
 * Used to retrieve a resource file.
 * Creates the file if it doesn't exist.
 * @param filename The name of the resource file.
 * @param replace Indicated if the file should be replaced with the default one. Defaults to false.
 * @return The input stream of the resource file.
 */
fun getResourceFileStream(filename: String, replace: Boolean = false): FileInputStream {
	val customConfigFile = File(plugin.dataFolder, filename)
	if (!customConfigFile.exists()) {
		customConfigFile.parentFile.mkdirs()
	}
	plugin.saveResource(filename, replace)

	return customConfigFile.inputStream()
}

/**
 * Loads and parse a resource config file.
 */
inline fun <reified T> config(filename: String, replace: Boolean = mainConfig.overrideOtherConfigFiles) =
	Yaml.default.decodeFromStream<T>(getResourceFileStream(filename, replace))
