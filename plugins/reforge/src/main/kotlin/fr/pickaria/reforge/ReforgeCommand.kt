package fr.pickaria.reforge

import fr.pickaria.economy.Credit
import fr.pickaria.economy.has
import fr.pickaria.economy.withdraw
import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.EnchantmentOffer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.inventory.EnchantingInventory
import java.util.*
import kotlin.random.Random

class ReforgeCommand : CommandExecutor, Listener {
	@EventHandler
	fun onEnchantItem(event: EnchantItemEvent) {
		with(event) {
			val inventory = (event.inventory as EnchantingInventory)
			inventory.secondary?.let {
				if (it.isAttributeItem()) {
					enchantsToAdd.clear()
					enchantsToAdd[GlowEnchantment.instance] = 1
					val slot = item.type.equipmentSlot
					val attributes = Attribute.values()
					val power = whichButton() + 1
					val percent = power / 3.0
					val count = Random.nextInt(1, (attributes.size * percent).toInt())
					attributes.shuffle()
					val attributesToAdd = attributes.slice(0..count)

					val title = getAttributeTitle(count)

					item.editMeta { meta ->
						meta.removeAttributeModifier(slot)
						meta.lore(listOf(title))
						attributesToAdd.forEach { attribute ->
							val amount = Random.nextDouble(-0.1, 0.1)

							meta.addAttributeModifier(
								attribute,
								AttributeModifier(
									UUID.randomUUID(),
									attribute.name,
									amount,
									AttributeModifier.Operation.ADD_SCALAR,
									slot
								)
							)
						}
					}

					enchanter.withdraw(Credit, expLevelCost.toDouble())
					it.amount -= power
				}
			}
		}
	}

	@EventHandler
	fun onPrepareItemEnchant(event: PrepareItemEnchantEvent) {
		with(event) {
			val inventory = (event.inventory as EnchantingInventory)
			inventory.secondary?.let {
				enchanter.sendMessage("Prepare ${it.amount}")
				if (it.isAttributeItem()) { // TODO: Verify event.item can be enchanted
					isCancelled = false
					offers[0] = null
					offers[1] = null
					offers[2] = null

					if (it.amount >= 1 && enchanter.has(Credit, 1000.0)) {
						offers[0] = EnchantmentOffer(GlowEnchantment.instance, 0, 1000)
					}
					if (it.amount >= 2 && enchanter.has(Credit, 2000.0)) {
						offers[1] = EnchantmentOffer(GlowEnchantment.instance, 0, 2000)
					}
					if (it.amount >= 3 && enchanter.has(Credit, 3000.0)) {
						offers[2] = EnchantmentOffer(GlowEnchantment.instance, 0, 3000)
					}
				}
			}
		}
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			sender.inventory.addItem(getAttributeItem()) // TODO: Use .give method
		}

		return true
	}
}