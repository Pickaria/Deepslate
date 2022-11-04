package fr.pickaria.shared

import io.papermc.paper.enchantments.EnchantmentRarity
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class GlowEnchantment(namespace: NamespacedKey): Enchantment(namespace) {
	companion object {
		lateinit var instance: Enchantment

		fun register(plugin: JavaPlugin) {
			val enchantmentNamespace = NamespacedKey(plugin, "enchantment")
			instance = GlowEnchantment(enchantmentNamespace)

			try {
				val field = Enchantment::class.java.getDeclaredField("acceptingNew")
				field.isAccessible = true
				field.set(null, true)

				if (getByKey(enchantmentNamespace) == null) {
					registerEnchantment(instance)
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	override fun translationKey(): String = ""

	override fun getName(): String  = "e"

	override fun getMaxLevel(): Int = 1

	override fun getStartLevel(): Int = 1

	override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.BREAKABLE

	override fun isTreasure(): Boolean  = false

	override fun isCursed(): Boolean = false

	override fun conflictsWith(other: Enchantment): Boolean  = false

	override fun canEnchantItem(item: ItemStack): Boolean  = false

	override fun displayName(level: Int): Component = Component.text("e")

	override fun isTradeable(): Boolean = false

	override fun isDiscoverable(): Boolean = false

	override fun getRarity(): EnchantmentRarity = EnchantmentRarity.COMMON

	override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0F

	override fun getActiveSlots(): MutableSet<EquipmentSlot> = mutableSetOf()
}