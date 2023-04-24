package fr.pickaria.model

import fr.pickaria.model.economy.BankAccounts
import fr.pickaria.model.job.Jobs
import fr.pickaria.model.market.Orders
import fr.pickaria.model.reward.DailyRewards
import fr.pickaria.model.teleport.Histories
import fr.pickaria.model.teleport.Homes
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
			BankAccounts, Jobs, Orders, DailyRewards, Histories, Homes
		)
	}

	transaction {
		SchemaUtils.statementsRequiredToActualizeScheme(
			BankAccounts, Jobs, Orders, DailyRewards, Histories, Homes
		) + SchemaUtils.addMissingColumnsStatements(
			BankAccounts, Jobs, Orders, DailyRewards, Histories, Homes
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