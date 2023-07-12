package fr.pickaria.controller.potion

import fr.pickaria.model.potion.Potion
import fr.pickaria.model.potion.potionNamespace
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

class PotionController(val model: Potion) {
	private val potionColor: Color by lazy {
		when (model.color) {
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
		model.duration / 60
	}

	private val seconds by lazy {
		model.duration % 60
	}

	private val lore: List<Component>
		get() = listOf<Component>(
			Component.text("${model.description} ($minutes:$seconds)", NamedTextColor.BLUE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text(""),
			Component.text("Si consommée :", NamedTextColor.DARK_PURPLE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("+${model.power}% ${model.effectName}", NamedTextColor.BLUE)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		)

	fun create(amount: Int = 1): ItemStack {
		val itemStack = ItemStack(Material.POTION, amount)
		val potion = (itemStack.itemMeta as PotionMeta)

		potion.color = potionColor
		potion.persistentDataContainer.set(potionNamespace, PersistentDataType.STRING, model.type.name)
		potion.addEnchant(GlowEnchantment.instance, 1, true)
		potion.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)

		potion.displayName(model.label)
		potion.lore(lore)

		itemStack.itemMeta = potion

		return itemStack
	}
}
