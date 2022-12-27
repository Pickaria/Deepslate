package fr.pickaria.controller.economy

import fr.pickaria.model.economy.Currency
import fr.pickaria.model.economy.toController
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import java.security.InvalidParameterException

class CurrencyBundle {
	companion object {
		internal operator fun invoke(result: ItemStack, init: Builder.() -> Unit) =
			Builder(result).apply(init).toMerchantRecipe()
	}

	class Builder(private val result: ItemStack) {
		private val currencies = mutableMapOf<Currency, Double>()

		infix fun Currency.to(value: Double) {
			if (value > 0) {
				currencies[this] = value
			}
		}

		infix fun Currency.to(value: Int) {
			if (value > 0) {
				currencies[this] = value.toDouble()
			}
		}

		fun toMerchantRecipe(): MerchantRecipe {
			if (currencies.isEmpty()) {
				throw InvalidParameterException("A merchant recipe must have at least 1 currency ingredient.")
			}

			if (currencies.size > 2) {
				throw InvalidParameterException("A merchant recipe cannot have more than 2 currencies, ${currencies.size} provided.")
			}

			return MerchantRecipe(result, Int.MAX_VALUE).apply {
				currencies.forEach { (currency, value) ->
					val controller = currency.toController()
					val items = controller.items(value)

					val item = if (items.size > 1) {
						controller.bundle(value, items)
					} else {
						items.first()
					}

					addIngredient(item)
				}
			}
		}
	}
}