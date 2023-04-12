package fr.pickaria.controller

import com.destroystokyo.paper.profile.ProfileProperty
import fr.pickaria.model.miniblocks.MiniBlock
import fr.pickaria.model.miniblocks.miniBlocksConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class MiniBlockController(val model: MiniBlock) {
	fun create(amount: Int = 1) = ItemStack(Material.PLAYER_HEAD, amount).apply {
		editMeta {
			val profile = Bukkit.createProfile(miniBlocksConfig.uuid)
			val property = ProfileProperty("textures", model.texture)
			profile.setProperty(property)
			(it as SkullMeta).playerProfile = profile
			it.displayName(
				Component.translatable(model.material.translationKey())
					.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			)
		}
	}
}

fun MiniBlock.toController() = MiniBlockController(this)