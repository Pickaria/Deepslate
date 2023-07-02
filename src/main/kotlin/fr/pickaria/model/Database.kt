package fr.pickaria.model

import fr.pickaria.model.economy.BankAccounts
import fr.pickaria.model.job.Jobs
import fr.pickaria.model.market.Orders
import fr.pickaria.model.reward.DailyRewards
import fr.pickaria.model.teleport.Histories
import fr.pickaria.model.teleport.Homes
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
	val database = Database.connect(
		url = mainConfig.databaseUrl.replace("\$path", path),
		user = mainConfig.databaseUser,
		password = mainConfig.databasePassword,
		driver = mainConfig.databaseDriver,
	)

	transaction {
		SchemaUtils.create(
			BankAccounts, Jobs, Orders, DailyRewards, Towns, Residents, Histories, Homes
		)
	}

	transaction {
		SchemaUtils.statementsRequiredToActualizeScheme(
			BankAccounts, Jobs, Orders, DailyRewards, Towns, Residents, Histories, Homes
		) + SchemaUtils.addMissingColumnsStatements(
			BankAccounts, Jobs, Orders, DailyRewards, Towns, Residents, Histories, Homes
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
