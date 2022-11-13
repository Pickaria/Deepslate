package fr.pickaria.economy

import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getLogger
import org.bukkit.OfflinePlayer

@GlobalCurrencyExtensions
val OfflinePlayer.balance: Double
	get() = economy.getBalance(this)

@GlobalCurrencyExtensions
infix fun OfflinePlayer.has(amount: Double): Boolean = has(Credit, amount)

/**
 * Withdraws money from the sender's account and deposits it into the recipient's account safely.
 */
@GlobalCurrencyExtensions
fun sendTo(sender: OfflinePlayer, recipient: OfflinePlayer, amount: Double): SendResponse = sendTo(Credit, sender, recipient, amount)

fun OfflinePlayer.balance(currency: Currency): Double = currency.economy.getBalance(this)

fun OfflinePlayer.has(currency: Currency, amount: Double): Boolean = currency.economy.has(this, amount)

fun OfflinePlayer.withdraw(currency: Currency, amount: Double): EconomyResponse = currency.economy.withdrawPlayer(this, amount)

fun OfflinePlayer.deposit(currency: Currency, amount: Double): EconomyResponse = currency.economy.depositPlayer(this, amount)

fun sendTo(currency: Currency, sender: OfflinePlayer, recipient: OfflinePlayer, amount: Double): SendResponse =
	if (sender.has(currency, amount)) {
		val withdrawResponse = sender.withdraw(currency,  amount)

		if (withdrawResponse.type == EconomyResponse.ResponseType.SUCCESS) {
			val depositResponse = recipient.deposit(currency, withdrawResponse.amount)

			if (depositResponse.type != EconomyResponse.ResponseType.SUCCESS) {
				// Try to refund
				val refund = sender.deposit(currency, withdrawResponse.amount)
				if (refund.type == EconomyResponse.ResponseType.FAILURE) {
					getLogger().severe("Can't refund player, withdrew amount: ${withdrawResponse.amount}")
					SendResponse.REFUND_ERROR
				}

				SendResponse.RECEIVE_ERROR
			} else {
				SendResponse.SUCCESS
			}
		} else {
			SendResponse.SEND_ERROR
		}
	} else {
		SendResponse.NOT_ENOUGH_MONEY
	}