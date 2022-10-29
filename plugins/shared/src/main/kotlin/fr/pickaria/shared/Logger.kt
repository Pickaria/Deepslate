package fr.pickaria.shared

import org.bukkit.Bukkit.getLogger
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs

/**
 * Usage: add `addLogger(BukkitLogger)` in a transaction.
 * https://github.com/JetBrains/Exposed/wiki/Getting-Started#getting-started
 */
object BukkitLogger : SqlLogger {
	override fun log(context: StatementContext, transaction: Transaction) {
		getLogger().info("SQL: ${context.expandArgs(transaction)}")
	}
}