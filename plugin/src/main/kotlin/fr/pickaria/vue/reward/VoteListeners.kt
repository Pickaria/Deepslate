package fr.pickaria.vue.reward

import com.vexsoftware.votifier.model.VotifierEvent
import fr.pickaria.controller.economy.deposit
import fr.pickaria.controller.reward.addDailyPoint
import fr.pickaria.controller.reward.dailyReward
import fr.pickaria.controller.reward.remainingToCollect
import fr.pickaria.model.economy.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VoteListeners : Listener {
	@EventHandler
	fun onVote(event: VotifierEvent) {
		with(event) {
			val offlinePlayer = Bukkit.getOfflinePlayer(vote.username)

			val message = if (offlinePlayer.dailyReward.remainingToCollect() > 0) {
				// TODO: Amount of points and key fragments per `serviceName`
				offlinePlayer.addDailyPoint(20)

				Component.text("Merci du soutien ! Pour te remercier, tu as obtenu", NamedTextColor.GRAY)
					.appendSpace()
					.append(Component.text("20 Points Quotidien", NamedTextColor.GOLD))
					.append(Component.text(".", NamedTextColor.GRAY))
			} else {
				offlinePlayer.deposit(Key, 0.2)

				Component.text("Merci du soutien ! Pour te remercier, tu as obtenu", NamedTextColor.GRAY)
					.appendSpace()
					.append(Component.text("2 Fragments de Clé", NamedTextColor.GOLD))
					.append(Component.text(".", NamedTextColor.GRAY))
			}

			offlinePlayer.player?.sendMessage(message)
		}
	}
}
