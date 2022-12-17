package fr.pickaria.reward

import fr.pickaria.database.models.PlayerReward
import fr.pickaria.deepslate.home.addToHome
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.menu
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*


internal fun rewardMenu() = menu("reward") {
	title = Component.text("Récompenses")
	rows = 3

	val playerReward = PlayerReward.get(opener.uniqueId)
	val field = WeekFields.of(Locale.FRANCE).dayOfWeek()
	val today = LocalDateTime.now()
	val firstDayOfWeek = today.with(field, 1)
	val lastCollected = playerReward?.lastReward

	for (x in 0..6) {
		val day = firstDayOfWeek.plusDays(x.toLong())
		val dayOfWeek = day.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)
			.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

		val endOfDay = day.with(LocalTime.MAX)
		val remaining = today.until(endOfDay, ChronoUnit.HOURS)
		val isCollected = lastCollected?.let {
			val date = day.toLocalDate()
			it.isAfter(date) || it.isEqual(date)
		} ?: false
		val canCollect = !isCollected && remaining in 0..24
		val unlockIn = today.until(day.with(LocalTime.MIN), ChronoUnit.HOURS)

		item {
			slot = x + 1
			title = Component.text(dayOfWeek)
			material = Material.BUNDLE
			lore {
				if (!canCollect) {
					description {
						if (0 <= unlockIn) {
							-"Se débloque dans $unlockIn heures."
						} else {
							-"Cette récompense ne peut plus être récupérée."
						}
					}
				} else {
					description {
						-"Encore $remaining heures pour récupérer la récompense."
					}
					leftClick = "Clic-gauche pour récupérer la récompense"
				}
			}

			if (canCollect) {
				leftClick = Result.NONE to "/collect $key"
			}

			editMeta { meta ->
				val bundle = (meta as BundleMeta)
				if (0 <= remaining && !isCollected) {
					bundle.addItem(ItemStack(Material.YELLOW_SHULKER_BOX))
				}

				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

				if (canCollect) {
					meta.addEnchant(GlowEnchantment.instance, 1, true)
				}
			}
		}
	}

	closeItem()
}.addToHome(Material.SHULKER_BOX, Component.text("Récompenses")) {
	description {
		-"Obtenir des récompenses journalières."
	}
}