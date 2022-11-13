package fr.pickaria.economy

import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getLogger
import org.bukkit.OfflinePlayer

@GlobalCurrencyExtensions
val OfflinePlayer.balance: Double
	get() = economy.getBalance(this)

@GlobalCurrencyExtensions
infix fun OfflinePlayer.has(amount: Double): Boolean = economy.has(this@has, amount)

@GlobalCurrencyExtensions
infix fun OfflinePlayer.withdraw(amount: Double): EconomyResponse = economy.withdrawPlayer(this, amount)

@GlobalCurrencyExtensions
infix fun OfflinePlayer.deposit(amount: Double): EconomyResponse = economy.depositPlayer(this, amount)

/**
 * Withdraws money from the sender's account and deposits it into the recipient's account safely.
 */
@GlobalCurrencyExtensions
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

@GlobalCurrencyExtensions
class SendMoney(private val sender: OfflinePlayer, private val amount: Double) {
	infix fun to(recipient: OfflinePlayer) = sendTo(sender, recipient, amount)
}

@Deprecated(
	"Prefer using sendTo() directly.",
	ReplaceWith("sendTo(sender, recipient, amount)", "fr.pickaria.economy.sendTo")
)
@GlobalCurrencyExtensions
infix fun OfflinePlayer.send(amount: Double) = SendMoney(this, amount)