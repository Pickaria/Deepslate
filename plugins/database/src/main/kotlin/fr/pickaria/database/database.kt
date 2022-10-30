package fr.pickaria.database

import fr.pickaria.database.models.BankAccounts
import fr.pickaria.database.models.Jobs
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

internal fun openDatabase(path: String): Database {
	// DB_CLOSE_DELAY: Reuse connection
	// AUTO_SERVER: Enable automatic mixed mode
	val database = Database.connect("jdbc:h2:$path;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE", "org.h2.Driver")

	transaction {
		SchemaUtils.create(
			BankAccounts, Jobs
		)
	}

	transaction {
		SchemaUtils.statementsRequiredToActualizeScheme(
			BankAccounts, Jobs
		) + SchemaUtils.addMissingColumnsStatements(
			BankAccounts, Jobs
		)
	}.forEach {
		transaction {
			try {
				TransactionManager.current().exec(it)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	return database
}