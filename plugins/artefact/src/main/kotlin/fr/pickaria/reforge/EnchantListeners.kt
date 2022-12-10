package fr.pickaria.reforge

import fr.pickaria.artefact.Config
import fr.pickaria.artefact.isAttributeItem
import fr.pickaria.artefact.updateRarity
import fr.pickaria.shared.GlowEnchantment
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.EnchantmentOffer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.inventory.EnchantingInventory
import org.bukkit.inventory.EquipmentSlot
import java.util.*
import kotlin.random.Random

class EnchantListeners: Listener {
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
					enchantsToAdd.clear()
					enchantsToAdd[GlowEnchantment.instance] = 1
					val slot = item.type.equipmentSlot
					val power = whichButton()

					item.editMeta { meta ->
						// Clear all previous attributes
						meta.removeAttributeModifier(slot)

						// Re-add default attributes
						item.type.getDefaultAttributeModifiers(slot).forEach { attribute, modifier ->
							val newModifier = AttributeModifier(
								UUID.randomUUID(),
								"default",
								modifier.amount,
								modifier.operation,
								modifier.slot
							)

							meta.addAttributeModifier(attribute, newModifier)
						}

						// Add random attributes
						AUTHORIZED_ATTRIBUTES.shuffled().slice(0..power).forEach { attribute ->
							val amount = Random.nextDouble(Config.minimumAttribute, Config.maximumAttribute)
							val modifier = AttributeModifier(
								UUID.randomUUID(),
								"custom",
								amount,
								AttributeModifier.Operation.ADD_SCALAR,
								slot
							)

							meta.addAttributeModifier(
								attribute,
								modifier
							)
						}
					}

					it.amount -= (power + 1)
				}
			}

			item.updateRarity()
		}
	}

	@EventHandler
	fun onPrepareItemEnchant(event: PrepareItemEnchantEvent) {
		with(event) {
			val inventory = (event.inventory as EnchantingInventory)
			inventory.secondary?.let {
				if (it.isAttributeItem()) { // TODO: Verify event.item can be enchanted
					isCancelled = false

					offers[0] = if (it.amount >= 1 && enchanter.level >= 1) {
						EnchantmentOffer(GlowEnchantment.instance, 0, 1)
					} else {
						null
					}

					offers[1] = if (it.amount >= 2 && enchanter.level >= 2) {
						EnchantmentOffer(GlowEnchantment.instance, 0, 2)
					} else {
						null
					}

					offers[2] = if (it.amount >= 3 && enchanter.level >= 3) {
						EnchantmentOffer(GlowEnchantment.instance, 0, 3)
					} else {
						null
					}
				}
			}
		}
	}
}