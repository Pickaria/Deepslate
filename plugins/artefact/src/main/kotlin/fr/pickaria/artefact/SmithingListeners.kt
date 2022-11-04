package fr.pickaria.artefact

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.SmithingInventory
import org.bukkit.persistence.PersistentDataType

internal class SmithingListeners : Listener {
	@EventHandler
	fun onPrepareResult(event: PrepareResultEvent) {
		if (event.inventory.type == InventoryType.SMITHING) {
			val inventory = (event.inventory as SmithingInventory)

			inventory.inputMineral?.let { getArtefactConfig(it) }?.let { artefact ->
				// If equipment can have artefact
				inventory.inputEquipment?.let {
					event.result = if (artefact.target.includes(it)) {
						val result = it.clone()

						result.itemMeta = result.itemMeta.apply {
							addEnchant(GlowEnchantment.instance, 1, true)
							lore(inventory.inputMineral!!.lore())
							persistentDataContainer.set(artefactNamespace, PersistentDataType.STRING, artefact.key.name)
						}

						result
					} else {
						null
					}
				}
			}
		}
	}

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		if (event.inventory.type == InventoryType.SMITHING && event.slotType == InventoryType.SlotType.RESULT) {
			event.currentItem?.let {
				if (isArtefact(it)) {
					// TODO: Handle shift-click
					event.cursor = event.currentItem
					event.inventory.clear()

					event.whoClicked.playSound(
						Sound.sound(Key.key("block.smithing_table.use"), Sound.Source.MASTER, 1f, 1f)
					)
					event.inventory.location?.let { loc ->
						loc.world.spawnParticle(Particle.END_ROD, loc.toCenterLocation(), 30, 1.0, 1.0, 1.0, 0.0)
					}
				}
			}
		}
	}

	/**
	 * Prevent other crafting recipes including an artefact.
	 */
	@EventHandler
	fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
		val containsArtefact = event.inventory.contents.filterNotNull().mapNotNull {
			getArtefactConfig(it)
		}.isNotEmpty()

		if (containsArtefact) {
			event.inventory.result = null
		}
	}
}