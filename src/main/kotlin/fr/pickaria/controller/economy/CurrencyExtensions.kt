package fr.pickaria.controller.economy

import fr.pickaria.model.economy.currencyNamespace
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.model.economy.toController
import fr.pickaria.model.economy.valueNamespace
import fr.pickaria.shared.GlowEnchantment
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Checks if the ItemStack is a valid currency.
 * The item must be enchanted, have an account name and a value.
 */
fun ItemStack.isCurrency(unsafe: Boolean = false): Boolean =
	itemMeta?.let {
		val hasCurrency = with(it.persistentDataContainer) {
			has(currencyNamespace, PersistentDataType.STRING) && has(valueNamespace, PersistentDataType.DOUBLE)
		}
		val isCurrency = currency != null
		val isEnchanted = unsafe || it.hasEnchants() && it.enchants.contains(GlowEnchantment.instance)
		hasCurrency && isEnchanted && isCurrency
	} ?: false

/**
 * The currency of the current item if any.
 */
val ItemStack.currency: CurrencyController?
	get() = economyConfig.currencies[account]?.toController()

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

infix fun OfflinePlayer.has(itemStack: ItemStack): Boolean =
	itemStack.currency?.let {
		has(it.model, itemStack.totalValue)
	} ?: false

infix fun OfflinePlayer.withdraw(itemStack: ItemStack): EconomyResponse =
	itemStack.currency?.let {
		if (has(it.model, itemStack.totalValue)) {
			withdraw(it.model, itemStack.totalValue)
		} else {
			EconomyResponse(
				0.0,
				balance(it.model),
				EconomyResponse.ResponseType.FAILURE,
				"Player do not have enough money."
			)
		}
	} ?: EconomyResponse(
		0.0,
		0.0,
		EconomyResponse.ResponseType.FAILURE,
		"Tried to deposit an item that is not a currency."
	)
