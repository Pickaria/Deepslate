package fr.pickaria.vue.reward

import com.vexsoftware.votifier.model.VotifierEvent
import fr.pickaria.controller.economy.deposit
import fr.pickaria.controller.reward.addDailyPoint
import fr.pickaria.controller.reward.dailyReward
import fr.pickaria.controller.reward.toInfo
import fr.pickaria.model.economy.Key
import fr.pickaria.shared.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VoteListeners : Listener {
	@EventHandler
	fun onVote(event: VotifierEvent) {
		with(event) {
			val offlinePlayer = Bukkit.getOfflinePlayer(vote.username)
			val info = offlinePlayer.dailyReward.toInfo()

			val message = if (info.remainingRewards > 0) {
				// TODO: Amount of points and key fragments per `serviceName`
				offlinePlayer.addDailyPoint(20)

				MiniMessage("<gray>Merci du soutien ! Pour te remercier, tu as obtenu <gold><amount> Points Quotidien</gold>.") {
					"amount" to 20
				}.message
			} else {
				offlinePlayer.deposit(Key, 0.2)

				MiniMessage("<gray>Merci du soutien ! Pour te remercier, tu as obtenu <gold><amount> Fragments de Clé</gold>.") {
					"amount" to 2
				}.message
			}

			offlinePlayer.player?.sendMessage(message)
		}
	}
}
