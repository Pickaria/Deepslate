package fr.pickaria.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
)

val mainConfig = config<MainConfig>("config.yml", false)
