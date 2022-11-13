package fr.pickaria.economy

import fr.pickaria.shared.GlowEnchantment
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

// TODO: Handle multiple currencies
abstract class CurrencyExtensions(private val currency: Currency) {
	// ItemStack extensions

	/**
	 * Checks if the ItemStack is a valid currency.
	 * The item must be enchanted, have an account name and a value.
	 */
	fun ItemStack.isCurrency(): Boolean =
		itemMeta?.let {
			val hasCurrency = with(it.persistentDataContainer) {
				has(currencyNamespace, PersistentDataType.STRING) && has(valueNamespace, PersistentDataType.DOUBLE)
			}
			val isCurrency = account == this@CurrencyExtensions.currency.economy.account
			val isEnchanted = it.hasEnchants() && it.enchants.contains(GlowEnchantment.instance)
			hasCurrency && isEnchanted && isCurrency
		} ?: false

	/**
	 * The currency account of the current item if any.
	 */
	val ItemStack.account: String?
		get() = itemMeta?.persistentDataContainer?.get(currencyNamespace, PersistentDataType.STRING)

	/**
	 * The value of one item.
	 */
	val ItemStack.value: Double
		get() = itemMeta?.persistentDataContainer?.get(valueNamespace, PersistentDataType.DOUBLE) ?: 0.0

	/**
	 * The total value of the item stack.
	 */
	val ItemStack.totalValue: Double
		get() = amount * value

	// OfflinePlayer extensions

	val OfflinePlayer.balance: Double
		get() = currency.economy.getBalance(this)

	infix fun OfflinePlayer.has(amount: Double): Boolean = currency.economy.has(this@has, amount)

	infix fun OfflinePlayer.withdraw(amount: Double): EconomyResponse = currency.economy.withdrawPlayer(this, amount)

	infix fun OfflinePlayer.deposit(amount: Double): EconomyResponse = currency.economy.depositPlayer(this, amount)

	infix fun OfflinePlayer.deposit(itemStack: ItemStack): EconomyResponse =
		if (itemStack.isCurrency()) {
			(this deposit itemStack.totalValue).also {
				itemStack.amount = 0
			}
		} else {
			EconomyResponse(
				0.0,
				this.balance,
				EconomyResponse.ResponseType.FAILURE,
				"Tried to deposit an item that is not a currency."
			)
		}

	fun sendTo(sender: OfflinePlayer, recipient: OfflinePlayer, amount: Double): SendResponse =
		if (sender has amount) {
			val withdrawResponse = sender withdraw amount

			if (withdrawResponse.type == EconomyResponse.ResponseType.SUCCESS) {
				val depositResponse = recipient deposit withdrawResponse.amount

				if (depositResponse.type != EconomyResponse.ResponseType.SUCCESS) {
					// Try to refund
					val refund = sender deposit withdrawResponse.amount
					if (refund.type == EconomyResponse.ResponseType.FAILURE) {
						Bukkit.getLogger().severe("Can't refund player, withdrew amount: ${withdrawResponse.amount}")
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
}