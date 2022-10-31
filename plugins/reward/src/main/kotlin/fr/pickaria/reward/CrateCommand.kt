package fr.pickaria.reward

import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.loot.LootContext
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType

internal class CrateCommand(private val plugin: JavaPlugin) : CommandExecutor, Listener {
	inner class CrateHolder : InventoryHolder {
		private lateinit var inventory: Inventory

		fun setInventory(inventory: Inventory) {
			this.inventory = inventory
		}

		override fun getInventory(): Inventory = inventory
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val item = ItemStack(Material.CHEST)

			item.editMeta {
				it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)

				it.displayName(
					Component.text("Sacoche de récompense épique", ItemRarity.EPIC.color)
						.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				)

				it.persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)

				// Chest meta
				val luck = sender.getPotionEffect(PotionEffectType.LUCK)?.amplifier ?: 0

				val lootContext = LootContext.Builder(sender.location)
					.luck(luck.toFloat())
					.build()

				val items = CustomLootTable(plugin).populateLoot(null, lootContext)

				val chest = (it as BlockStateMeta).blockState as Chest
				chest.inventory.contents = items.toTypedArray()
			}

			sender.inventory.addItem(item)
		}

		return true
	}

	@EventHandler
	fun onOpenBundle(event: PlayerInteractEvent) {
		if (event.hasItem()) {
			event.item?.let {
				if (it.type == Material.CHEST && it.itemMeta?.persistentDataContainer?.has(namespace) == true) {
					val chest = (it.itemMeta as BlockStateMeta).blockState as Chest
					val player = event.player

					val holder = CrateHolder()
					val inventory = Bukkit.createInventory(
						holder,
						InventoryType.DROPPER,
						Component.text("Récompense", NamedTextColor.GOLD, TextDecoration.BOLD)
					)
					holder.inventory = inventory

					inventory.contents = chest.inventory.contents

					player.openInventory(inventory)

					event.isCancelled = true
				}
			}
		}
	}

	@EventHandler
	fun onCrateClosed(event: InventoryCloseEvent) {
		with(event) {
			if (inventory.holder is CrateHolder) {
				player.inventory.addItem(ItemStack(Material.ACACIA_PLANKS))
				player.inventory.addItem(*inventory.contents.filterNotNull().toTypedArray()).forEach {
					val location = player.eyeLocation
					val item = location.world.dropItem(location, it.value)
					item.velocity = location.direction.multiply(0.25)
				}
			}
		}
	}
}