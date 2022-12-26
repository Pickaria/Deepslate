package fr.pickaria.model

import fr.pickaria.model.economy.BankAccounts
import fr.pickaria.model.job.Jobs
import fr.pickaria.model.market.Orders
import fr.pickaria.model.town.Residents
import fr.pickaria.model.town.Towns
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

fun openDatabase(path: String): Database {
	// DB_CLOSE_DELAY: Reuse connection
	// AUTO_SERVER: Enable automatic mixed mode
	val database = Database.connect("jdbc:h2:$path;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE", "org.h2.Driver")

	transaction {
		SchemaUtils.create(
			BankAccounts, Jobs, Orders, Towns, Residents
		)
	}

	transaction {
		SchemaUtils.statementsRequiredToActualizeScheme(
			BankAccounts, Jobs, Orders, Towns, Residents
		) + SchemaUtils.addMissingColumnsStatements(
			BankAccounts, Jobs, Orders, Towns, Residents
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

/**
 * Usage: add `addLogger(BukkitLogger)` in a transaction.
 * https://github.com/JetBrains/Exposed/wiki/Getting-Started#getting-started
 */
object BukkitLogger : SqlLogger {
	override fun log(context: StatementContext, transaction: Transaction) {
		Bukkit.getLogger().info("SQL: ${context.expandArgs(transaction)}")
	}
}

fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
