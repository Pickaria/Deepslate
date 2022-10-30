package fr.pickaria.database.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal const val DEFAULT_ACCOUNT = "default"

internal object BankAccounts : Table() {
	val playerUuid = uuid("player_uuid")
	val accountName = varchar("account_name", 16).default(DEFAULT_ACCOUNT)
	val balance = double("balance").default(0.0)

	override val primaryKey = PrimaryKey(playerUuid, accountName)
}

class BankAccount(private val row: ResultRow) {
	companion object {
		fun create(playerId: UUID, account: String = DEFAULT_ACCOUNT, amount: Double = 0.0) = transaction {
			BankAccounts.insert {
				it[playerUuid] = playerId
				it[accountName] = account
				it[balance] = amount
			}.resultedValues?.firstOrNull()
		}?.let { BankAccount(it) }

		fun get(playerId: UUID, account: String = DEFAULT_ACCOUNT) = transaction {
			BankAccounts.select {
				(BankAccounts.playerUuid eq playerId) and (BankAccounts.accountName eq account)
			}.firstOrNull()
		}?.let { BankAccount(it) }

		// Case-specific functions
		fun top(page: Int = 0, account: String = DEFAULT_ACCOUNT, limit: Int = 8): List<BankAccount> = transaction {
			val offset = (page * limit).toLong()

			BankAccounts.select { BankAccounts.accountName eq account }
				.orderBy(BankAccounts.balance, SortOrder.DESC)
				.limit(limit, offset)
				.map {
					BankAccount(it)
				}
		}

		fun count(account: String = DEFAULT_ACCOUNT): Long = transaction {
			BankAccounts.select { BankAccounts.accountName eq account }
				.count()
		}
	}

	private val whereClause =
		{ (BankAccounts.playerUuid eq this.playerUuid) and (BankAccounts.accountName eq this.accountName) }

	val playerUuid: UUID
		get() = row[BankAccounts.playerUuid]

	val accountName: String
		get() = row[BankAccounts.accountName]

	var balance: Double
		get() = row[BankAccounts.balance]
		set(value) = transaction {
			BankAccounts.update({ whereClause() }) {
				it[BankAccounts.balance] = value
			}
		}
}