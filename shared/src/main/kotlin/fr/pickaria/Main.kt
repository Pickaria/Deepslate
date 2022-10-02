package fr.pickaria

import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.serialization.SerializationClassMappingTypeService

class Main: JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		logger.info("Shared plugin loaded!")

		System.setProperty("org.litote.mongo.mapping.service", SerializationClassMappingTypeService::class.qualifiedName!!)

		testDatabase()
	}
}