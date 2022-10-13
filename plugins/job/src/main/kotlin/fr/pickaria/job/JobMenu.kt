package fr.pickaria.job

import fr.pickaria.menu.BaseMenu
import fr.pickaria.shared.models.Job
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class JobMenu(title: String, opener: HumanEntity?, previousMenu: BaseMenu?, size: Int = 54) :
	BaseMenu(title, opener, previousMenu, size) {

	class Factory: BaseMenu.Factory("§6§lMétiers", Material.WOODEN_PICKAXE) {
		override fun create(opener: HumanEntity?, previousMenu: BaseMenu?, size: Int): BaseMenu = JobMenu(title, opener, previousMenu, size)
	}

	init {
		fillMaterial = Material.GREEN_STAINED_GLASS_PANE
	}

	override fun initMenu() {
		var x = 1

		jobConfig.jobs.forEach { (key, job) ->
			val lore = mutableListOf<String>()
			job.description.forEach {
				lore.add("§7${it}")
			}

			opener?.let { opener ->
				Job.get(opener.uniqueId, key)?.let {
					val level = jobController.getLevelFromExperience(job, it.experience)
					lore.add("§6Expérience totale :§7 ${it.experience}")
					lore.add("§6Niveau :§7 $level")
				}

				if (!jobController.hasJob(opener.uniqueId, key)) {
					lore.add("Clic-gauche pour rejoindre le métier")
				} else {
					lore.add("Clic-droit pour quitter le métier")
				}
			}

			setMenuItem {
				this.x = x++
				y = 2
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