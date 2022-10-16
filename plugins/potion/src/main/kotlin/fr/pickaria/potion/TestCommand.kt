package fr.pickaria.potion

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration

internal class TestCommand(plugin: JavaPlugin) : CommandExecutor, Listener {
	private val namespace = NamespacedKey(plugin, "potion")

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val amount = try {
				args?.getOrNull(0)?.toInt() ?: 1
			} catch (_: NumberFormatException) {
				1
			}

			val itemStack = ItemStack(Material.POTION, amount)
			val potion = (itemStack.itemMeta as PotionMeta)

			potion.color = Color.YELLOW
			potion.persistentDataContainer.set(namespace, PersistentDataType.STRING, PotionType.JOB_EXPERIENCE_BOOST.name)
			potion.addEnchant(Enchantment.MENDING, 1, true)
			potion.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)

			val name = Component.text("Potion de Métier", NamedTextColor.WHITE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			potion.displayName(name)

			val duration = Duration.ofSeconds(PotionType.JOB_EXPERIENCE_BOOST.duration)
			val minutes = duration.toMinutesPart()
			val seconds = duration.toSecondsPart()
			val durationString = "$minutes:$seconds"

			val lore = listOf<Component>(
				Component.text("Boost d'expérience ($durationString)", NamedTextColor.BLUE)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				Component.text(""),
				Component.text("Si consommée :", NamedTextColor.DARK_PURPLE)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
				Component.text("+100% expérience", NamedTextColor.BLUE)
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			)

			potion.lore(lore)

			itemStack.itemMeta = potion

			sender.inventory.addItem(itemStack)

			return true
		}

		return false
	}

	@EventHandler
	fun onPlayerConsumeEvent(event: PlayerItemConsumeEvent) {
		if (event.item.type == Material.POTION) {
			val potion = (event.item.itemMeta as PotionMeta)

			potion.persistentDataContainer.get(namespace, PersistentDataType.STRING)?.let {
				try {
					PotionType.valueOf(it)
				} catch (_: IllegalArgumentException ) {
					null
				}?.let { effect ->
					potionController.addPotionEffect(effect, event.player)
				}
			}
		}
	}
}