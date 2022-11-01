package fr.pickaria.job

import fr.pickaria.database.models.Job
import net.kyori.adventure.text.Component
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.Bukkit.getLogger
import kotlin.math.pow

private val lastPayment = mutableMapOf<Player, Long>()

private fun jobPayPlayer(player: Player, amount: Double): Boolean {
	val now = System.currentTimeMillis() // This uses 32 bit, alert for future us

	if (now - (lastPayment[player] ?: 0L) < jobConfig.lastPaymentDelay) {
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

fun jobPayPlayer(player: Player, amount: Double, config: JobConfig.Configuration, experienceToGive: Int = 0) {
	Job.get(player.uniqueId, config.key)?.let {
		val level = jobController.getLevelFromExperience(config, it.experience)
		val amountToPay = amount * config.revenueIncrease.pow(level)
		val amountIncrease = amountToPay + amountToPay * it.ascentPoints * jobConfig.moneyIncrease

		if (jobPayPlayer(player, amountIncrease) && experienceToGive > 0) {
			jobController.addExperienceAndAnnounce(player, config, experienceToGive)
		}
	}
}
