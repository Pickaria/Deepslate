package fr.pickaria.vue.artefact

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import fr.pickaria.controller.artefact.artefact
import fr.pickaria.controller.artefact.isArtefact
import fr.pickaria.controller.artefact.setArtefact
import fr.pickaria.controller.reforge.updateDefaultAttributes
import fr.pickaria.controller.reforge.updateLore
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.SmithingInventory

internal class SmithingListeners : Listener {
	@EventHandler
	fun onPrepareResult(event: PrepareResultEvent) {
		if (event.inventory.type == InventoryType.SMITHING) {
			val inventory = (event.inventory as SmithingInventory)

			inventory.inputMineral?.artefact?.let { artefact ->
				// If equipment can have artefact
				inventory.inputEquipment?.let {
					event.result = if (artefact.target.includes(it)) {
						it.clone().setArtefact(artefact)
					} else {
						null
					}
				}
			}
		}

		event.result?.updateDefaultAttributes()
		event.result?.updateLore()
	}

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		if (event.inventory.type == InventoryType.SMITHING && event.slotType == InventoryType.SlotType.RESULT) {
			event.currentItem?.let {
				if (it.isArtefact()) {
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
		with(event.inventory) {
			val containsArtefact = contents.filterNotNull().mapNotNull {
				it.artefact
			}.isNotEmpty()

			if (containsArtefact) {
				result = null
			}

			result?.updateDefaultAttributes()
			result?.updateLore()
		}
	}
}