package fr.pickaria.shared

import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.dao.flushCache
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class Main : JavaPlugin() {
	private lateinit var database: Database

	override fun onEnable() {
		super.onEnable()

		database = openDatabase(dataFolder.absolutePath + "/database")

		getCommand("test")?.setExecutor(TestCommand()) ?: logger.warning("Could not register `test` command.")

		logger.info("Shared plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()

		transaction {
			flushCache()
			TransactionManager.closeAndUnregister(database)
		}
	}
}