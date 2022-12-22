package fr.pickaria.potion

import fr.pickaria.shared.ConfigProvider
import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType

class PotionConfig : ConfigProvider() {
	val label: Component by this
	val color: BossBar.Color by this
	val duration: Int by this
	val description: String by this
	val effectName: String by this
	val power: Int by this

	private val potionColor: Color by lazy {
		when (color) {
			BossBar.Color.PINK -> Color.FUCHSIA
			BossBar.Color.BLUE -> Color.BLUE
			BossBar.Color.RED -> Color.RED
			BossBar.Color.GREEN -> Color.GREEN
			BossBar.Color.YELLOW -> Color.YELLOW
			BossBar.Color.PURPLE -> Color.PURPLE
			BossBar.Color.WHITE -> Color.WHITE
		}
	}

	private val minutes by lazy {
		duration / 60
	}

	private val seconds by lazy {
		duration % 60
	}

	private val lore: List<Component>
		get() = listOf<Component>(
			Component.text("$description ($minutes:$seconds)", NamedTextColor.BLUE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text(""),
			Component.text("Si consommée :", NamedTextColor.DARK_PURPLE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("+$power% $effectName", NamedTextColor.BLUE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		)

	fun create(amount: Int): ItemStack {
		val itemStack = ItemStack(Material.POTION, amount)
		val potion = (itemStack.itemMeta as PotionMeta)

		potion.color = potionColor
		potion.persistentDataContainer.set(namespace, PersistentDataType.STRING, section!!.name)
		potion.addEnchant(GlowEnchantment.instance, 1, true)
		potion.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

		potion.displayName(label)
		potion.lore(lore)

		itemStack.itemMeta = potion

		return itemStack
	}
}
