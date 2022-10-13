package fr.pickaria.job

import fr.pickaria.menu.BaseMenu
import fr.pickaria.shared.models.Job
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class JobMenu(title: String, opener: HumanEntity, previousMenu: BaseMenu?, size: Int = 54) :
	BaseMenu(title, opener, previousMenu, size) {

	class Factory: BaseMenu.Factory("§6§lMétiers", Material.WOODEN_PICKAXE) {
		override fun create(opener: HumanEntity, previousMenu: BaseMenu?, size: Int): BaseMenu = JobMenu(title, opener, previousMenu, size)
	}

	init {
		fillMaterial = Material.GREEN_STAINED_GLASS_PANE
	}

	override fun initMenu() {
		val playerJobs = Job.get(opener.uniqueId).associateBy { it.job }

		var x = 4 - playerJobs.size / 2
		val y = if (playerJobs.isEmpty()) {
			2
		} else {
			3
		}

		playerJobs.forEach { (key, job) ->
			if (job.active) {
				val config = jobConfig.jobs[key]

				config?.let {
					setMenuItem {
						this.x = x++
						this.y = 1
						material = it.icon
						name = "Métier actuel"
						lore = listOf("§7${config.label}", "§6Récompense en attente de récupération")
						isEnchanted = true
					}
				}
			}
		}

		x = 1

		jobConfig.jobs.forEach { (key, job) ->

			val lore = mutableListOf<String>()
			job.description.forEach {
				lore.add("§7${it}")
			}

			playerJobs[key]?.let {
				val level = jobController.getLevelFromExperience(job, it.experience)
				lore.add("§6Expérience totale :§7 ${it.experience}")
				lore.add("§6Niveau :§7 $level")
			} ?: lore.add("§7Métier pas encore exercé")

			if (!jobController.hasJob(opener.uniqueId, key)) {
				lore.add("Clic-gauche pour rejoindre le métier")
			} else {
				lore.add("Clic-droit pour quitter le métier")
			}

			setMenuItem {
				this.x = x++
				this.y = y
				material = job.icon
				name = job.label
				this.lore = lore
				callback = {
					with (it.whoClicked as Player) {
						if (it.isLeftClick) {
							this.chat("/job join $key")
						} else if (it.isRightClick) {
							this.chat("/job leave $key")
						}
					}
				}
			}
		}
	}
}