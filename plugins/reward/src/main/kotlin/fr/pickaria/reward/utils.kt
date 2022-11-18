package fr.pickaria.reward

import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private val description = Config.rewardDescription.map {
	MiniMessage(it).message.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
}

internal fun createReward(key: String, amount: Int = 1) = Config.rewards[key]?.let { reward ->
	ItemStack(reward.material, amount).also { item ->
		item.editMeta {
			it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
			it.displayName(reward.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
			it.lore(description)
			it.persistentDataContainer.set(namespace, PersistentDataType.STRING, key)
		}
	}
}