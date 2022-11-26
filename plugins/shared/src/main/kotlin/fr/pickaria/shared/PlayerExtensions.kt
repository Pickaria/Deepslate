package fr.pickaria.shared

import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun HumanEntity.give(vararg items: ItemStack) {
	inventory.addItem(*items).forEach {
		val location = eyeLocation
		val item = location.world.dropItem(location, it.value)
		item.velocity = location.direction.multiply(0.3)
	}
}

fun Inventory.count(material: Material) = contents.filterNotNull().filter { it.type == material }.sumOf { it.amount }