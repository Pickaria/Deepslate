package fr.pickaria.shared

import fr.pickaria.shared.models.BankAccounts
import fr.pickaria.shared.models.Jobs
import fr.pickaria.shared.models.SellOrders
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

internal fun openDatabase(path: String): Database {
	// DB_CLOSE_DELAY: Reuse connection
	// AUTO_SERVER: Enable automatic mixed mode
	val database = Database.connect("jdbc:h2:$path;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE", "org.h2.Driver")

	transaction {
		SchemaUtils.create(
			BankAccounts, Jobs, SellOrders
		)
	}

	transaction {
		SchemaUtils.statementsRequiredToActualizeScheme(
			BankAccounts, Jobs, SellOrders
		) + SchemaUtils.addMissingColumnsStatements(
			BankAccounts, Jobs, SellOrders
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