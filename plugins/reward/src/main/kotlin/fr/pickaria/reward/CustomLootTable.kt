package fr.pickaria.reward

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTable
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

internal class CustomLootTable(private val plugin: JavaPlugin): LootTable {
	override fun getKey(): NamespacedKey = NamespacedKey(plugin, "custom_loot_table")

	override fun populateLoot(random: Random?, context: LootContext): MutableCollection<ItemStack> {
		return mutableListOf(ItemStack(Material.ACACIA_PLANKS, 64), ItemStack(Material.ACACIA_PLANKS, 64), ItemStack(Material.ACACIA_PLANKS, 64))
	}

	override fun fillInventory(inventory: Inventory, random: Random?, context: LootContext) {
		val rand = random ?: Random()

		populateLoot(rand, context).forEach {
			val slot = rand.nextInt(8)
			println(slot)
			inventory.setItem(slot, it)
		}
	}
}