package fr.pickaria.controller.reward

import fr.pickaria.model.reward.Reward
import fr.pickaria.model.reward.rewardNamespace
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class RewardController(val model: Reward) {
	fun create(amount: Int = 1) =
		ItemStack(model.material, amount).also { item ->
			item.editMeta { meta ->
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
				meta.displayName(model.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
				val description = model.description.map {
					MiniMessage(it).message.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				}
				meta.lore(description)
				meta.persistentDataContainer.set(rewardNamespace, PersistentDataType.STRING, model.type)
			}
		}
}