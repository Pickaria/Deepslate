package fr.pickaria.shared.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal const val DEFAULT_ACCOUNT = "default"

internal object BankAccounts : Table() {
	val playerUuid = uuid("player_uuid")
	val accountName = varchar("account_name", 16).default(DEFAULT_ACCOUNT)
	val amount = double("amount").default(0.0)

	override val primaryKey = PrimaryKey(playerUuid, accountName)
}

class BankAccount(private val row: ResultRow) {
	companion object {
		fun create(playerId: UUID, account: String = DEFAULT_ACCOUNT, amount: Double = 0.0) = transaction {
			BankAccounts.insert {
				it[playerUuid] = playerId
				it[accountName] = account
				it[this.amount] = amount
			}.resultedValues?.firstOrNull()
		}?.let { BankAccount(it) }

		fun get(playerId: UUID, account: String = DEFAULT_ACCOUNT) = transaction {
			BankAccounts.select {
				(BankAccounts.playerUuid eq playerId) and (BankAccounts.accountName eq account)
			}.firstOrNull()
		}?.let { BankAccount(it) }
	}

	val id: UUID
		get() = row[BankAccounts.playerUuid]

	val accountName: String
		get() = row[BankAccounts.accountName]

	var balance: Double
		get() = row[BankAccounts.amount]
		set(value) = transaction {
			BankAccounts.update {
				row[amount] = value
			}
		}
}