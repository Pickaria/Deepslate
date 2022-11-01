package fr.pickaria.job

import fr.pickaria.job.events.JobAscentEvent
import fr.pickaria.job.events.JobLevelUpEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JobListener : Listener {
	@EventHandler
	fun onJobLevelUp(event: JobLevelUpEvent) {
		val player = event.player
		val level = event.level
		val label = event.job.label
		val type = event.type

		player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
		player.sendMessage("§7Vous montez niveau §6$level§7 dans le métier §6$label§7.")

		if (type == LevelUpType.ASCENT_UNLOCKED) {
			val mainTitle = Component.text("Ascension débloquée", NamedTextColor.GOLD)
			val subtitle = Component.text("Vous avez débloqué l'ascension pour le métier $label !", NamedTextColor.GRAY)
			val title = Title.title(mainTitle, subtitle)

			player.showTitle(title)
		} else if (type == LevelUpType.MAX_LEVEL_REACHED) {
			val mainTitle = Component.text("Niveau maximum atteint", NamedTextColor.GOLD)
			val subtitle = Component.text("Vous avez atteint le niveau maximum dans le métier $label !", NamedTextColor.GRAY)
			val title = Title.title(mainTitle, subtitle)

			player.showTitle(title)
		}
	}

	@EventHandler
	fun onJobAscent(event: JobAscentEvent) {
		val player = event.player
		val label = event.job.label
		val ascentPoints = event.ascentPoints

		val experienceBoost = jobConfig.experienceIncrease * ascentPoints * 100
		val moneyBoost = jobConfig.moneyIncrease * ascentPoints * 100

		player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
		player.sendMessage(
			"§7Vous avez effectué une ascension et collecté §6$ascentPoints§7 points d'ascension dans le métier §6$label§7.\n" +
			"§7Vous obtenez un bonus de §6$experienceBoost%§7 d'expérience ainsi que §6$moneyBoost%§7 de revenus supplémentaires."
		)
	}
}