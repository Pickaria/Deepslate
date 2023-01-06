package fr.pickaria.controller.economy

import fr.pickaria.model.economy.BankAccount
import fr.pickaria.model.economy.BankAccounts
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import net.milkbowl.vault.economy.EconomyResponse.ResponseType
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DecimalFormat
import kotlin.math.absoluteValue

class Economy(
	private val currencyNameSingular: String,
	private val currencyNamePlural: String,
	private val account: String = "default",
	format: String = "0.00"
) : AbstractEconomy() {
	private val formatter = DecimalFormat(format).apply {
		decimalFormatSymbols = decimalFormatSymbols.apply {
			groupingSeparator = ' '
		}
	}

	// Constants methods

	override fun getName(): String = account

	override fun fractionalDigits(): Int = -1

	override fun format(amount: Double): String = if (amount <= 1.0) {
		"${formatter.format(amount)} ${currencyNameSingular()}"
	} else {
		"${formatter.format(amount)} ${currencyNamePlural()}"
	}

	override fun isEnabled(): Boolean = true

	override fun hasBankSupport(): Boolean = false

	override fun currencyNamePlural(): String = currencyNamePlural

	override fun currencyNameSingular(): String = currencyNameSingular

	// Logic methods

	private fun getAccount(player: OfflinePlayer): BankAccount? = try {
		transaction {
			BankAccount.find {
				(BankAccounts.playerUuid eq player.uniqueId) and (BankAccounts.accountName eq account)
			}.single()
		}
	} catch (_: NoSuchElementException) {
		null // Empty
	} catch (_: IllegalArgumentException) {
		null // More than one
	}

	override fun hasAccount(player: OfflinePlayer): Boolean = getAccount(player) != null

	override fun getBalance(player: OfflinePlayer): Double = getAccount(player)?.balance ?: 0.0

	override fun has(player: OfflinePlayer, amount: Double): Boolean = getBalance(player) >= amount

	/**
	 * Deposits a negative amount into the player's account (basically withdraws).
	 * Any balance check must be made independently.
	 */
	override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse = depositPlayer(player, -amount)

	/**
	 * Deposits an amount into the player's account.
	 * Any balance check must be made independently.
	 */
	override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse = transaction {
		try {
			val account = BankAccount.find {
				(BankAccounts.playerUuid eq player.uniqueId) and (BankAccounts.accountName eq account)
			}.single()
			account.balance += amount
			EconomyResponse(amount, account.balance, ResponseType.SUCCESS, "")
		} catch (_: NoSuchElementException) {
			// Account not found, create a new one
			val account = BankAccount.new {
				playerUuid = player.uniqueId
				accountName = account
				balance = amount
			}
			EconomyResponse(amount.absoluteValue, account.balance, ResponseType.SUCCESS, "")
		} catch (_: IllegalArgumentException) {
			// Too many accounts (???)
			EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Too many account for this player.")
		}
	}

	override fun createPlayerAccount(player: OfflinePlayer): Boolean = transaction {
		BankAccount.new {
			playerUuid = player.uniqueId
			accountName = account
		}

		true
	}

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

	@Deprecated("Deprecated in Java")
	override fun isBankOwner(name: String?, playerName: String?): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	@Deprecated("Deprecated in Java")
	override fun isBankMember(name: String?, playerName: String?): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse =
		EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "We do not support bank accounts!")

	// Deprecated methods

	@Deprecated("Deprecated in Java")
	override fun hasAccount(playerName: String): Boolean = hasAccount(getOfflinePlayer(playerName))

	@Deprecated("Deprecated in Java")
	override fun hasAccount(playerName: String?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun getBalance(playerName: String): Double = getBalance(getOfflinePlayer(playerName))

	@Deprecated("Deprecated in Java")
	override fun getBalance(playerName: String?, world: String?): Double {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun has(playerName: String, amount: Double): Boolean = has(getOfflinePlayer(playerName), amount)

	@Deprecated("Deprecated in Java")
	override fun has(playerName: String?, worldName: String?, amount: Double): Boolean {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse =
		withdrawPlayer(getOfflinePlayer(playerName), amount)

	@Deprecated("Deprecated in Java")
	override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun depositPlayer(playerName: String, amount: Double): EconomyResponse =
		depositPlayer(getOfflinePlayer(playerName), amount)

	@Deprecated("Deprecated in Java")
	override fun depositPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
		TODO("Not yet implemented")
	}

	@Deprecated("Deprecated in Java")
	override fun createPlayerAccount(playerName: String): Boolean =
		createPlayerAccount(getOfflinePlayer(playerName))

	@Deprecated("Deprecated in Java")
	override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
		TODO("Not yet implemented")
	}
}