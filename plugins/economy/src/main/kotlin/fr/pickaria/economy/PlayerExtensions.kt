package fr.pickaria.economy

import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getLogger
import org.bukkit.OfflinePlayer

infix fun OfflinePlayer.has(amount: Double): Boolean = economy.has(this, amount)

infix fun OfflinePlayer.withdraw(amount: Double): EconomyResponse = economy.withdrawPlayer(this, amount)

infix fun OfflinePlayer.deposit(amount: Double): EconomyResponse = economy.depositPlayer(this, amount)

enum class SendResponse {
	RECEIVE_ERROR,
	REFUND_ERROR,
	SUCCESS,
	SEND_ERROR,
	NOT_ENOUGH_MONEY;
}

/**
 * Withdraws money from the sender's account and deposits it into the recipient's account safely.
 */
fun sendTo(sender: OfflinePlayer, recipient: OfflinePlayer, amount: Double): SendResponse =
	if (sender has amount) {
		val withdrawResponse = sender withdraw amount

		if (withdrawResponse.type == EconomyResponse.ResponseType.SUCCESS) {
			val depositResponse = recipient deposit withdrawResponse.amount

			if (depositResponse.type != EconomyResponse.ResponseType.SUCCESS) {
				// Try to refund
				val refund = sender deposit withdrawResponse.amount
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

class SendMoney(private val sender: OfflinePlayer, private val amount: Double) {
	infix fun to(recipient: OfflinePlayer) = sendTo(sender, recipient, amount)
}

infix fun OfflinePlayer.send(amount: Double) = SendMoney(this, amount)