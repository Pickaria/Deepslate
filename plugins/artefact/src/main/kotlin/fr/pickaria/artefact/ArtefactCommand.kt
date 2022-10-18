package fr.pickaria.artefact

import fr.pickaria.shared.GlowEnchantment
import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType

internal class ArtefactCommand : CommandExecutor, Listener, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			args?.getOrNull(0)?.let {
				Artefact.valueOf(it.uppercase())
			}?.let { artefact ->
				val material: Material = when (artefact.rarity) {
					ItemRarity.COMMON -> Material.COPPER_INGOT
					ItemRarity.UNCOMMON -> Material.IRON_INGOT
					ItemRarity.RARE -> Material.GOLD_INGOT
					ItemRarity.EPIC -> Material.NETHERITE_INGOT
				}

				val itemStack = ItemStack(material)

				val itemMeta = itemStack.itemMeta
				if (itemMeta is LeatherArmorMeta) {
					itemMeta.setColor(artefact.color)
					itemMeta.addItemFlags(ItemFlag.HIDE_DYE)
				}

				itemMeta.addEnchant(GlowEnchantment.instance, 1, true)
				itemMeta.persistentDataContainer.set(namespace, PersistentDataType.STRING, artefact.name)
				itemMeta.displayName(
					Component.text("Réceptacle d'artefact", artefact.rarity.color)
						.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				)

				itemMeta.lore(listOf(artefact.displayName) + artefact.lore)

				itemStack.itemMeta = itemMeta

				sender.inventory.addItem(itemStack)
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>?
	): MutableList<String> {
		return Artefact.values().map { it.name.lowercase() }.toMutableList()
	}
}