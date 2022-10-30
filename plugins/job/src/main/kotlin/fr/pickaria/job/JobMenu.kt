package fr.pickaria.job

import fr.pickaria.database.models.Job
import fr.pickaria.menu.BaseMenu
import fr.pickaria.menu.MenuLore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import java.math.RoundingMode
import java.text.DecimalFormat

class JobMenu(title: Component, opener: HumanEntity, previousMenu: BaseMenu?) :
	BaseMenu(title, opener, previousMenu, size = 54) {

	class Factory(material: Material = Material.WOODEN_PICKAXE) :
		BaseMenu.Factory(Component.text("Métiers", NamedTextColor.GOLD, TextDecoration.BOLD), material) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?): BaseMenu =
			JobMenu(title, opener, previousMenu)
	}

	private val decimalFormat = DecimalFormat("#")

	init {
		fillMaterial = Material.GREEN_STAINED_GLASS_PANE
		decimalFormat.roundingMode = RoundingMode.FLOOR
	}

	override fun initMenu() {
		val playerJobs = Job.get(opener.uniqueId).mapNotNull {
			jobConfig.jobs[it.job]?.let { config -> config to it }
		}.toMap()

		val activeJobs = playerJobs.filter { (_, job) -> job.active }

		var x = 4 - activeJobs.size / 2
		val y = if (activeJobs.isEmpty()) {
			2
		} else {
			3
		}

		activeJobs.forEach { (config, job) ->
			val ascentPoints = jobController.getAscentPoints(job, config)

			val lore = MenuLore.build {
				leftClick = if (ascentPoints > 0) "Clic-gauche pour effectuer une ascension" else null
				keyValues = mapOf("Points d'ascension à récupérer" to ascentPoints)
			}

			setMenuItem {
				this.x = x++
				this.y = 1
				material = config.icon
				name = Component.text("Métier actuel : ${config.label}", NamedTextColor.GRAY)
				this.lore = lore
				if (ascentPoints > 0) {
					isEnchanted = true
					leftClick = {
						jobController.ascentJob(it.whoClicked as Player, config, job, ascentPoints)
						inventory.close()
					}
				}
			}
		}

		x = 1

		jobConfig.jobs.forEach { (key, config) ->
			val job = playerJobs[config]
			val isCurrentJob = job?.active ?: false

			val lore = MenuLore.build {
				description = config.description

				job?.let {
					val level = jobController.getLevelFromExperience(config, it.experience)

					keyValues = if (it.ascentPoints > 0) {
						mapOf(
							"Niveau" to "$level",
							"Bonus d'expérience" to "+${decimalFormat.format(it.ascentPoints * jobConfig.experienceIncrease * 100)}%",
							"Bonus de revenus" to "+${decimalFormat.format(it.ascentPoints * jobConfig.moneyIncrease * 100)}%",
						)
					} else {
						mapOf(
							"Niveau" to "$level",
						)
					}
				}

				if (isCurrentJob) {
					rightClick = "Clic-droit pour quitter le métier"
				} else {
					leftClick = "Clic-gauche pour rejoindre le métier"
				}
			}

			setMenuItem {
				this.x = x++
				this.y = y
				material = config.icon
				name = Component.text(config.label)
				this.lore = lore

				leftClick = {
					if (!isCurrentJob) {
						(it.whoClicked as Player).chat("/job join $key")
						inventory.close()
					}
				}
				rightClick = {
					if (isCurrentJob) {
						(it.whoClicked as Player).chat("/job leave $key")
						inventory.close()
					}
				}
			}
		}
	}
}