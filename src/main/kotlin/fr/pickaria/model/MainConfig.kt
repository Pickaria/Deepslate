package fr.pickaria.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.WorldCreator

@Serializable
data class MainConfig(
	@SerialName("override_other_config_file")
	val overrideOtherConfigFiles: Boolean = false,

	@SerialName("database_url")
	val databaseUrl: String,

	@SerialName("database_user")
	val databaseUser: String,

	@SerialName("database_password")
	val databasePassword: String,

	@SerialName("database_driver")
	val databaseDriver: String,

	@SerialName("lobby_world_name")
	val lobbyWorldName: String,

	@SerialName("overworld_name")
	val overworldName: String,
) {
	val lobbyWorld = Bukkit.getWorld(lobbyWorldName) ?: WorldCreator(lobbyWorldName).createWorld()
	val overworldWorld = Bukkit.getWorld(overworldName) ?: WorldCreator(overworldName).createWorld()
}

val mainConfig = config<MainConfig>("config.yml", false)
