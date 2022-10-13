package fr.pickaria.economy

import fr.pickaria.shared.models.BankAccount
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import java.text.DecimalFormat

class PickariaEconomy : Economy {
	// Constants methods

	override fun getName(): String = "Pickaria economy"

	override fun fractionalDigits(): Int = -1

	override fun format(amount: Double): String =
		if (amount <= 1.0) {
			"${DecimalFormat("0.00").format(amount)} ${currencyNameSingular()}"
		} else {
			"${DecimalFormat("0.00").format(amount)} ${currencyNamePlural()}"
		}

	override fun isEnabled(): Boolean = true

	override fun hasBankSupport(): Boolean = false

	override fun currencyNamePlural(): String = "$"

	override fun currencyNameSingular(): String = "$"

	// Logic methods

	override fun hasAccount(player: OfflinePlayer): Boolean =
		BankAccount.get(player.uniqueId)?.let {
			true
		} ?: false

	override fun getBalance(player: OfflinePlayer): Double =
		BankAccount.get(player.uniqueId)?.balance ?: 0.0

	override fun has(player: OfflinePlayer, amount: Double): Boolean =
		getBalance(player) > amount

	override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse =
		player.uniqueId.let {
			if (!hasAccount(player)) {
				BankAccount.create(it)
			} else {
				BankAccount.get(it)
			}
		}?.let {
			it.balance -= amount
			EconomyResponse(0.0, it.balance, EconomyResponse.ResponseType.SUCCESS, "")
		} ?: EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "")

	override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse =
		player.uniqueId.let {
			if (!hasAccount(player)) {
				BankAccount.create(it)
			} else {
				BankAccount.get(it)
			}
		}?.let {
			it.balance += amount
			EconomyResponse(0.0, it.balance, EconomyResponse.ResponseType.SUCCESS, "")
		} ?: EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "")

	override fun createPlayerAccount(player: OfflinePlayer): Boolean =
		BankAccount.create(player.uniqueId)?.let {
			true
		} ?: false

	// World methods

	override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}

	override fun depositPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun withdrawPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun has(player: OfflinePlayer?, worldName: String?, amount: Double): Boolean {
		TODO("Not yet implemented")
	}

	override fun hasAccount(player: OfflinePlayer?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}

	override fun getBalance(player: OfflinePlayer?, world: String?): Double {
		TODO("Not yet implemented")
	}

	// Bank methods

	override fun createBank(name: String?, player: OfflinePlayer?): EconomyResponse {
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

	override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse {
		TODO("Not yet implemented")
	}

	override fun getBanks(): MutableList<String> {
		TODO("Not yet implemented")
	}

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
	override fun createBank(name: String?, player: String?): EconomyResponse {
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