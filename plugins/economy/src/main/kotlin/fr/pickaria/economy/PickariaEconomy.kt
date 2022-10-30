package fr.pickaria.economy

import fr.pickaria.database.models.BankAccount
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import net.milkbowl.vault.economy.EconomyResponse.ResponseType
import org.bukkit.OfflinePlayer
import java.text.DecimalFormat

class PickariaEconomy : AbstractEconomy() {
	private val formatter = DecimalFormat("0.00")

	// Constants methods

	override fun getName(): String = "Pickaria economy"

	override fun fractionalDigits(): Int = -1

	override fun format(amount: Double): String =
		if (amount <= 1.0) {
			"${formatter.format(amount)} ${currencyNameSingular()}"
		} else {
			"${formatter.format(amount)} ${currencyNamePlural()}"
		}

	override fun isEnabled(): Boolean = true

	override fun hasBankSupport(): Boolean = false

	override fun currencyNamePlural(): String = Config.currencyNamePlural

	override fun currencyNameSingular(): String = Config.currencyNameSingular

	// Logic methods

	override fun hasAccount(player: OfflinePlayer): Boolean =
		BankAccount.get(player.uniqueId)?.let {
			true
		} ?: false

	override fun getBalance(player: OfflinePlayer): Double =
		BankAccount.get(player.uniqueId)?.balance ?: 0.0

	override fun has(player: OfflinePlayer, amount: Double): Boolean =
		getBalance(player) >= amount

	override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse =
		player.uniqueId.let {
			if (!hasAccount(player)) {
				BankAccount.create(it)
			} else {
				BankAccount.get(it)
			}
		}?.let {
			it.balance -= amount
			EconomyResponse(amount, it.balance, ResponseType.SUCCESS, "")
		} ?: EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "")

	override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse =
		player.uniqueId.let {
			if (!hasAccount(player)) {
				BankAccount.create(it)
			} else {
				BankAccount.get(it)
			}
		}?.let {
			it.balance += amount
			EconomyResponse(amount, it.balance, ResponseType.SUCCESS, "")
		} ?: EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "")

	override fun createPlayerAccount(player: OfflinePlayer): Boolean =
		BankAccount.create(player.uniqueId)?.let {
			true
		} ?: false

	// Bank methods

	override fun createBank(name: String?, player: String?): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	override fun deleteBank(name: String?): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	override fun bankBalance(name: String?): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	override fun bankHas(name: String?, amount: Double): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	override fun bankWithdraw(name: String?, amount: Double): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	override fun bankDeposit(name: String?, amount: Double): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	override fun getBanks(): MutableList<String> = mutableListOf()

	// Deprecated methods

	@Deprecated("Deprecated in Java")
	override fun hasAccount(playerName: String?): Boolean {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun hasAccount(playerName: String?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun getBalance(playerName: String?): Double {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun getBalance(playerName: String?, world: String?): Double {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun has(playerName: String?, amount: Double): Boolean {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun has(playerName: String?, worldName: String?, amount: Double): Boolean {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun withdrawPlayer(playerName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun depositPlayer(playerName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun depositPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun createPlayerAccount(playerName: String?): Boolean {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}

	override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
		TODO("Not yet implemented")
	}
}