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
import org.bukkit.inventory.SmithingInventory
import org.bukkit.persistence.PersistentDataType

internal class SmithingListeners : Listener {
	@EventHandler
	fun onPrepareResult(event: PrepareResultEvent) {
		if (event.inventory.type == InventoryType.SMITHING) {
			val inventory = (event.inventory as SmithingInventory)

			inventory.inputMineral?.let { getArtefact(it) }
				?.let { artefact ->
					// If equipment can have artefact
					inventory.inputEquipment?.let {
						if (artefact.target.includes(it)) {
							val result = it.clone()
							result.itemMeta = result.itemMeta.apply {
								addEnchant(GlowEnchantment.instance, 1, true)
								lore(inventory.inputMineral!!.lore())
								persistentDataContainer.set(namespace, PersistentDataType.STRING, artefact.name)
							}
							event.result = result
						} else {
							event.result = null
						}
					}
				}
		}
	}

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		if (event.inventory.type == InventoryType.SMITHING) {
			val inventory = (event.inventory as SmithingInventory)

			if (event.slotType == InventoryType.SlotType.RESULT) {
				event.currentItem?.let {
					if (isArtefact(it)) {
						// TODO: HAndle shift-click
						event.cursor = event.currentItem
						inventory.clear()

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
	}
}