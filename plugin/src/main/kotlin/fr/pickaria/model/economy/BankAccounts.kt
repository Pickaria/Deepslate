package fr.pickaria.model.economy

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object BankAccounts : IntIdTable() {
	val playerUuid = uuid("player_uuid")
	val accountName = varchar("account_name", 16).default(economyConfig.defaultAccount)
	val balance = double("balance").default(0.0)

	init {
		index(true, playerUuid, accountName)
	}
}

class BankAccount(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<BankAccount>(BankAccounts)

	var playerUuid by BankAccounts.playerUuid
	var accountName by BankAccounts.accountName
	var balance by BankAccounts.balance
}
