package fr.pickaria.vue.reforge

import fr.pickaria.controller.reforge.*
import fr.pickaria.model.reforge.reforgeConfig
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.grantAdvancement
import org.bukkit.GameMode
import org.bukkit.enchantments.EnchantmentOffer
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.inventory.EnchantingInventory
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class EnchantListeners : Listener {
	@EventHandler
	fun onEnchantItem(event: EnchantItemEvent) {
		with(event) {
			val inventory = (event.inventory as EnchantingInventory)

			inventory.secondary?.let {
				if (it.isAttributeItem()) {
					item.clearAttributes()

					enchantsToAdd.clear()
					enchantsToAdd[GlowEnchantment.instance] = 1

					// Add random attributes
					val level = reforgeConfig.levels.values.filter { level ->
						expLevelCost in level.minimumLevel..level.maximumLevel
					}.random()

					val minAttributes = min(level.minimumAttributes, level.attributes.size)
					val maxAttributes = min(level.maximumAttributes, level.attributes.size)
					val attributeCount = if (maxAttributes == minAttributes) {
						minAttributes
					} else {
						Random.nextInt(minAttributes, maxAttributes)
					} - 1

					val validAttributes = level.attributes.filter { attribute ->
						attribute.target.includes(item)
					}
					val sliceMax = min(validAttributes.size - 1, attributeCount)
					val attributes = validAttributes.shuffled().slice(0..sliceMax)

					for (attribute in attributes) {
						item.addRandomAttributeModifier(attribute)
					}

					it.amount -= (whichButton() + 1)
					if (enchanter.gameMode != GameMode.CREATIVE) {
						enchanter.level -= expLevelCost
					}

					item.artefactRarity = level

					enchanter.playSound(reforgeConfig.enchantSound)
				}
			}

			item.updateDefaultAttributes(enchantsToAdd)
			item.updateLore()

			item.artefactRarity.advancement?.let {
				enchanter.grantAdvancement(it)
			}
		}
	}

	@EventHandler
	fun onPrepareItemEnchant(event: PrepareItemEnchantEvent) {
		with(event) {
			// Check if item can be enchanted
			if (!EnchantmentTarget.ALL.includes(item)) return

			val inventory = (event.inventory as EnchantingInventory)
			inventory.secondary?.let {
				if (it.isAttributeItem()) {
					isCancelled = false

					val bonus = if (enchantmentBonus > 0) Random.nextInt(0, enchantmentBonus) else 0
					val base = (Random.nextInt(1, 8) + floor(enchantmentBonus / 2.0) + bonus)
					val top = floor(max(base / 3, 1.0))
					val middle = floor((base / 2) / 3 + 1)
					val bottom = floor(max(base, enchantmentBonus * 2.0))

					offers[0] = EnchantmentOffer(GlowEnchantment.instance, 1, top.toInt())
					offers[1] = EnchantmentOffer(GlowEnchantment.instance, 1, middle.toInt())
					offers[2] = EnchantmentOffer(GlowEnchantment.instance, 1, bottom.toInt())
				}
			}
		}
	}
}