package fr.pickaria.model.economy

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

private const val DEFAULT_ACCOUNT = "credits"

@Deprecated("Use BankAccount instead")
class LegacyBankAccount(private val row: ResultRow) {
	companion object {
		// Case-specific functions
		fun top(page: Int = 0, account: String = DEFAULT_ACCOUNT, limit: Int = 8): List<LegacyBankAccount> =
			transaction {
				val offset = (page * limit).toLong()

				BankAccounts.select { BankAccounts.accountName eq account }
					.orderBy(BankAccounts.balance, SortOrder.DESC)
					.limit(limit, offset)
					.map {
						LegacyBankAccount(it)
					}
			}

		fun count(account: String = DEFAULT_ACCOUNT): Long = transaction {
			BankAccounts.select { BankAccounts.accountName eq account }
				.count()
		}
	}

	val balance: Double
		get() = row[BankAccounts.balance]
}
