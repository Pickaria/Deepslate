package fr.pickaria.job

import fr.pickaria.newmenu.closeItem
import fr.pickaria.newmenu.fill
import fr.pickaria.newmenu.menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import java.math.RoundingMode
import java.text.DecimalFormat

private val decimalFormat = DecimalFormat("#.#").also {
	it.roundingMode = RoundingMode.FLOOR
}

internal fun jobMenu() = menu("job") {
	title = Component.text("Métiers", NamedTextColor.GOLD, TextDecoration.BOLD)
	rows = 6

	val playerJobs = opener.jobs().mapNotNull { it.config?.let { config -> config to it } }.toMap()
	val activeJobs = playerJobs.filter { (_, job) -> job.active }

	var x = 4 - activeJobs.size / 2
	val y = if (activeJobs.isEmpty()) 2 else 3

	activeJobs.forEach { (config, job) ->
		val ascentPoints = getAscentPoints(job, config)

		item {
			position = x++ to 1
			material = config.icon
			title = Component.text("Métier actuel : ${config.label}", NamedTextColor.GRAY)
			lore {
				leftClick = if (ascentPoints > 0) "Clic-gauche pour effectuer une ascension" else null
				keyValues {
					"Points d'ascension à récupérer" to ascentPoints
				}
			}
			leftClick = "/job ascent ${config.key}"
			editMeta {
				it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
				if (ascentPoints > 0) {
					it.addEnchant(Enchantment.MENDING, 1, true)
				}
			}
		}
	}

	x = 1

	jobConfig.jobs.forEach { (key, config) ->
		val job = playerJobs[config]
		val isCurrentJob = job?.active ?: false

		item {
			position = x++ to y
			material = config.icon
			title = Component.text(config.label).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

			if (isCurrentJob) rightClick = "/job leave $key"
			else leftClick = "/job join $key"

			lore {
				description {
					config.description.forEach { -it }
				}

				job?.let {
					val level = getLevelFromExperience(config, it.experience)

					keyValues {
						"Niveau" to "$level"

						if (it.ascentPoints > 0) {
							"Points d'ascension" to it.ascentPoints
							"Bonus d'expérience" to "+${decimalFormat.format(it.ascentPoints * jobConfig.experienceIncrease * 100)}%"
							"Bonus de revenus" to "+${decimalFormat.format(it.ascentPoints * jobConfig.moneyIncrease * 100)}%"
						}
					}
				}

				if (isCurrentJob) rightClick = "Clic-droit pour quitter le métier"
				else leftClick = "Clic-gauche pour rejoindre le métier"
			}

			editMeta {
				it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
			}
		}
	}

	closeItem()
	fill(Material.GREEN_STAINED_GLASS_PANE, true)
}