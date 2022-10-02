package fr.pickaria

import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse

class PickariaEconomy: AbstractEconomy() {
	override fun isEnabled(): Boolean {
		TODO("Not yet implemented")
	}

	override fun getName(): String {
		TODO("Not yet implemented")
	}

	override fun hasBankSupport(): Boolean {
		TODO("Not yet implemented")
	}

	override fun fractionalDigits(): Int {
		TODO("Not yet implemented")
	}

	override fun format(amount: Double): String {
		TODO("Not yet implemented")
	}

	override fun currencyNamePlural(): String {
		TODO("Not yet implemented")
	}

	override fun currencyNameSingular(): String {
		TODO("Not yet implemented")
	}

	override fun hasAccount(playerName: String?): Boolean {
		TODO("Not yet implemented")
	}

	override fun hasAccount(playerName: String?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}

	override fun getBalance(playerName: String?): Double {
		TODO("Not yet implemented")
	}

	override fun getBalance(playerName: String?, world: String?): Double {
		TODO("Not yet implemented")
	}

	override fun has(playerName: String?, amount: Double): Boolean {
		TODO("Not yet implemented")
	}

	override fun has(playerName: String?, worldName: String?, amount: Double): Boolean {
		TODO("Not yet implemented")
	}

	override fun withdrawPlayer(playerName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun depositPlayer(playerName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun depositPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun createBank(name: String?, player: String?): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun deleteBank(name: String?): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun bankBalance(name: String?): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun bankHas(name: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun getBanks(): MutableList<String> {
		TODO("Not yet implemented")
	}

	override fun createPlayerAccount(playerName: String?): Boolean {
		TODO("Not yet implemented")
	}

	override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}
}