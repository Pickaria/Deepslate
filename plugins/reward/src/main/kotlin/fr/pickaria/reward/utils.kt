package fr.pickaria.reward

import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun createReward(key: String, amount: Int = 1) = Config.rewards[key]?.let { reward ->
	ItemStack(reward.material, amount).also { item ->
		item.editMeta { meta ->
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
			meta.displayName(reward.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
			val description = reward.description.map {
				MiniMessage(it).message.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			}
			meta.lore(description)
			meta.persistentDataContainer.set(namespace, PersistentDataType.STRING, key)
		}
	}
}