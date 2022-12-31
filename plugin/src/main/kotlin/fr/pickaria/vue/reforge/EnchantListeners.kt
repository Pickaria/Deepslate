package fr.pickaria.vue.reforge

import fr.pickaria.controller.reforge.*
import fr.pickaria.model.advancements.CustomAdvancement
import fr.pickaria.model.reforge.reforgeConfig
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.grantAdvancement
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.EnchantmentOffer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.inventory.EnchantingInventory

class EnchantListeners : Listener {
	companion object {
		private val AUTHORIZED_ATTRIBUTES = listOf(
			Attribute.GENERIC_MAX_HEALTH,
			Attribute.GENERIC_FOLLOW_RANGE,
			Attribute.GENERIC_KNOCKBACK_RESISTANCE,
			Attribute.GENERIC_MOVEMENT_SPEED,
			Attribute.GENERIC_ATTACK_DAMAGE,
			Attribute.GENERIC_ATTACK_KNOCKBACK,
			Attribute.GENERIC_ATTACK_SPEED,
			Attribute.GENERIC_ARMOR,
			Attribute.GENERIC_ARMOR_TOUGHNESS,
			Attribute.GENERIC_LUCK,
		)
	}

	@EventHandler
	fun onEnchantItem(event: EnchantItemEvent) {
		with(event) {
			val inventory = (event.inventory as EnchantingInventory)

			inventory.secondary?.let {
				if (it.isAttributeItem()) {
					item.clearAttributes()

					enchantsToAdd.clear()
					val power = whichButton()

					// Add random attributes
					for (attribute in AUTHORIZED_ATTRIBUTES.shuffled().slice(0..power)) {
						item.addRandomAttributeModifier(attribute)
					}

					it.amount -= (power + 1)
					enchanter.level -= (power + 1)

					enchanter.playSound(reforgeConfig.enchantSound)

					CustomAdvancement.FIRST_REFORGE.grant(enchanter)
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
			if (!item.type.canBeEnchanted) return

			val inventory = (event.inventory as EnchantingInventory)
			inventory.secondary?.let {
				if (it.isAttributeItem()) {
					isCancelled = false

					offers[0] = EnchantmentOffer(GlowEnchantment.instance, 1, 1)
					offers[1] = EnchantmentOffer(GlowEnchantment.instance, 1, 2)
					offers[2] = EnchantmentOffer(GlowEnchantment.instance, 1, 3)
				}
			}
		}
	}
}