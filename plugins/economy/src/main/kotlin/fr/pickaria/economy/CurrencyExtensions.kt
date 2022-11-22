package fr.pickaria.economy

import fr.pickaria.shared.GlowEnchantment
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class CurrencyExtensions(vararg currencies: Currency) {
	private val currencies = currencies.associateBy { it.account }
	private val accounts = currencies.map { it.account }

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
			val isCurrency = account in accounts
			val isEnchanted = it.hasEnchants() && it.enchants.contains(GlowEnchantment.instance)
			hasCurrency && isEnchanted && isCurrency
		} ?: false

	/**
	 * The currency of the current item if any.
	 */
	val ItemStack.currency: Currency?
		get() = if (isCurrency()) {
			currencies[account]
		} else {
			null
		}

	// OfflinePlayer extensions

	/**
	 * Deposits an item into the appropriate account and sends a feedback message.
	 */
	infix fun OfflinePlayer.deposit(itemStack: ItemStack): EconomyResponse =
		itemStack.currency?.let {
			val response = it.collect(this, itemStack)
			if (this is Player) {
				it.message(this, response.amount)
			}
			response
		} ?: EconomyResponse(
			0.0,
			0.0,
			EconomyResponse.ResponseType.FAILURE,
			"Tried to deposit an item that is not a currency."
		)

	infix fun OfflinePlayer.silentDeposit(itemStack: ItemStack): EconomyResponse =
		itemStack.currency?.collect(this, itemStack) ?: EconomyResponse(
			0.0,
			0.0,
			EconomyResponse.ResponseType.FAILURE,
			"Tried to deposit an item that is not a currency."
		)
}