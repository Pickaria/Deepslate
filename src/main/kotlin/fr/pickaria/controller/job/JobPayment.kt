package fr.pickaria.controller.job

import fr.pickaria.model.economy.Credit
import fr.pickaria.model.job.Job
import fr.pickaria.model.job.JobModel
import fr.pickaria.model.job.jobConfig
import net.kyori.adventure.text.Component
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getLogger
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.pow

private val lastPayment = mutableMapOf<Player, Long>()

private fun jobPayPlayer(player: Player, amount: Double): Boolean {
	val now = System.currentTimeMillis() // This uses 32 bit, alert for future us

	if (now - (lastPayment[player] ?: 0L) < jobConfig.lastPaymentDelay) {
		return false
	}
	lastPayment[player] = now

	return if (Credit.economy.depositPlayer(player, amount).type === EconomyResponse.ResponseType.SUCCESS) {
		player.sendActionBar(Component.text("§6+ ${Credit.economy.format(amount)}"))

		val location = player.location
		player.playSound(location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 1f)
		true
	} else {
		getLogger().severe("Error in payment of player ${player.name} [UUID: ${player.uniqueId}]. Amount: $amount")
		false
	}
}

fun jobPayPlayer(player: Player, amount: Double, config: Job, experienceToGive: Int = 0) {
	JobModel.get(player.uniqueId, config.type)?.let {
		val level = getLevelFromExperience(config, it.experience)
		val amountToPay = amount * config.revenueIncrease.pow(level)
		val amountIncrease = amountToPay + amountToPay * it.ascentPoints * jobConfig.ascent.moneyIncrease

		if (jobPayPlayer(player, amountIncrease) && experienceToGive > 0) {
			addExperienceAndAnnounce(player, config, experienceToGive)
		}
	}
}
