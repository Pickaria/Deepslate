package fr.pickaria.vue.reward

import fr.pickaria.controller.home.addToHome
import fr.pickaria.controller.reward.*
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toController
import fr.pickaria.shared.GlowEnchantment
import kotlinx.datetime.toKotlinLocalDate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.BundleMeta
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*

private val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM")
	.withLocale(Locale.FRENCH)

fun rewardMenu() = menu("reward") {
	val playerReward = opener.dailyReward

	val remaining = playerReward.remainingToCollect()
	val remainingMessage = if (remaining > 1) {
		Component.text("($remaining restantes)")
	} else {
		Component.text("($remaining restante)")
	}

	title = Component.text("Récompenses", NamedTextColor.DARK_BLUE, TextDecoration.BOLD)
		.appendSpace()
		.append(
			remainingMessage.color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
		)
	rows = 4

	val field = WeekFields.of(Locale.FRANCE).dayOfWeek()
	val today = LocalDateTime.now()
	val firstDayOfWeek = today.with(field, 1)

	for (x in 0..6) {
		val day = firstDayOfWeek.plusDays(x.toLong())
		val date = day.toLocalDate().toKotlinLocalDate()
		val dayOfWeek = formatter.format(day)
			.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

		val canCollect = playerReward.canCollect(date)
		val endOfDay = day.with(LocalTime.MAX)
		val remaining = today.until(endOfDay, ChronoUnit.HOURS)
		val isCollected = playerReward.remainingToCollect(date) == 0
		val isDayToCollect = !isCollected && remaining in 0..24
		val unlockIn = today.until(day.with(LocalTime.MIN), ChronoUnit.HOURS)

		val collected = playerReward.collected(date)
		val rewards = playerReward.rewards(date)

		item {
			position = x + 1 to 1
			title = Component.text(dayOfWeek)
			material = Material.BUNDLE
			lore {
				if (!isDayToCollect) {
					description {
						if (0 > unlockIn) {
							-"Cette récompense ne peut plus être récupérée."
						}
					}
					if (0 <= unlockIn) {
						keyValues {
							"Déblocage dans" to "$unlockIn heures"
						}
					}
				} else {
					keyValues {
						"Temps restants" to "$remaining heures"
						"Points collectés" to "${playerReward.dailyPoints(date)} / ${rewardConfig.dailyPointsToCollect}"
						"Récompenses récupérées" to "$collected / ${playerReward.rewardCount(date)}"
						"Série" to playerReward.streak(date)
					}
				}
			}

			if (canCollect > 0) {
				leftClick = Result.CLOSE to "/reward claim"
			}

			editMeta { meta ->
				val bundle = (meta as BundleMeta)
				if (0 <= remaining && !isCollected) {
					rewards.drop(collected).forEach {
						bundle.addItem(it.toController().create())
					}
				}

				meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

				if (canCollect > 0) {
					meta.addEnchant(GlowEnchantment.instance, 1, true)
				}
			}
		}
	}

	fill(Material.WHITE_STAINED_GLASS_PANE, true)
	closeItem()
}.addToHome(Material.SHULKER_BOX, Component.text("Récompenses")) {
	description {
		-"Obtenir des récompenses journalières."
	}
}