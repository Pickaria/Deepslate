package fr.pickaria.job

import fr.pickaria.economy.economy
import fr.pickaria.shared.models.Job
import net.kyori.adventure.text.Component
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.Bukkit.getLogger
import kotlin.math.pow

val lastPayment = mutableMapOf<Player, Long>()
const val LAST_PAYMENT_DELAY = 200L

fun jobPayPlayer(player: Player, amount: Double): Boolean {
	val now = System.currentTimeMillis() // This uses 32 bit, alert for future us

	if (now - (lastPayment[player] ?: 0L) < LAST_PAYMENT_DELAY) {
		return false
	}
	lastPayment[player] = now

	return if (economy.depositPlayer(player, amount).type === EconomyResponse.ResponseType.SUCCESS) {
		player.sendActionBar(Component.text("§6+ ${economy.format(amount)}"))

		val location = player.location
		player.playSound(location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 1f)
		true
	} else {
		getLogger().severe("Error in payment of player ${player.name} [UUID: ${player.uniqueId}]. Amount: $amount")
		false
	}
}

fun jobPayPlayer(player: Player, amount: Double, job: JobConfig.Configuration, experienceToGive: Int = 0) {
	val experience = Job.get(player.uniqueId, job.key)?.experience ?: 0
	val level = jobController.getLevelFromExperience(job, experience)

	if (jobPayPlayer(player, amount * job.revenueIncrease.pow(level)) && experienceToGive > 0) {
		jobController.addExperienceAndAnnounce(player, job, experienceToGive)
	}
}
