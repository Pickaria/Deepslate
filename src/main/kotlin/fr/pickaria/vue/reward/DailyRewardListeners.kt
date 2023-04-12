package fr.pickaria.vue.reward

import fr.pickaria.controller.reward.events.DailyRewardReadyEvent
import fr.pickaria.model.reward.rewardConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class DailyRewardListeners : Listener {
	@EventHandler
	fun onDailyRewardReady(event: DailyRewardReadyEvent) {
		with(event) {
			offlinePlayer.player?.let {
				it.sendMessage(rewardConfig.dailyRewardIsReady)
				it.playSound(rewardConfig.dailyRewardSound)
			}
		}
	}
}
