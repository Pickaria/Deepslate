package fr.pickaria.vue.teleport

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import createMetaDataTpTag
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.model.teleport.*
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction


@CommandAlias("home")
@CommandPermission("pickaria.command.home")
class HomeTeleport(private val plugin: JavaPlugin, manager: PaperCommandManager) : BaseCommand() {

	private fun homeFind(player: Player, name: String) = transaction {
		Home.find {
			(Homes.playerUuid eq player.uniqueId) and (Homes.homeName eq name)

		}.firstOrNull()

	}

	init {
		manager.commandContexts.registerContext(Home::class.java) {
			val arg: String = it.popFirstArg()

			homeFind(it.player, arg)
		}

		manager.commandCompletions.registerCompletion("ownhome") { context ->
			transaction {
				Home.find { (Homes.playerUuid eq context.player.uniqueId) and (Homes.homeName like "${context.input}%") }
					.map { it.homeName }
			}
		}

	}

	private val TAG = "HAS_TP_ONGOING"


	@Default
	@CommandCompletion("@ownhome")
	@Description("Vous téléporte aléatoirement autour du spawn.")
	fun onDefault(player: Player, home: Home) {

		val now = Clock.System.now().plus(teleportConfig.delayBetweenTeleports, DateTimeUnit.SECOND)
			.toLocalDateTime(TimeZone.currentSystemDefault())

		val tpTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

		val history = transaction {
			History.find {
				Histories.playerUuid eq player.uniqueId
			}.firstOrNull()
		}

		val cost = 10.0

		val containsTaskTag = player.scoreboardTags.contains(TAG)

		val canTeleport = history?.let {
			it.lastTeleport < tpTime
		} ?: true

		if (homeFind(player, name) == null) {
			throw ConditionFailedException("Cette résidence n'existe pas.")
		}
		if (!player.hasMetadata(TAG)) {
			if (canTeleport) {
				if (player.has(Credit, cost)) {
					player.sendMessage(teleportConfig.messageBeforeTeleport)
					MiniMessage("<gray>La téléportation vous a couté <gold><amount><gray>.") {
						"amount" to Credit.economy.format(cost)
					}.send(player)

					createMetaDataTpTag(plugin, player)
					Bukkit.getScheduler().runTaskLater(plugin, Runnable {
						player.withdraw(Credit, cost)
						var homeLocation = getHomeLocation(player, name)
						player.teleport(homeLocation)
					}, 120L)
					transaction {
						history?.let {
							it.lastTeleport = now
						} ?: run {
							History.new {
								playerUuid = player.uniqueId
								lastTeleport = now
							}
						}
					}
				} else {
					player.sendMessage(economyConfig.notEnoughMoney)
				}
			} else {
				throw ConditionFailedException("Patientez avant de vous téléporter de nouveau.")

			}
		} else {
			throw ConditionFailedException("Une téléportation est déjà en cours")
		}
	}

	private fun getHomeLocation(player: Player, name: String): Location {


		val home = homeFind(player, name)

		val newLocationX = home?.locationX?.toDouble()
		val newLocationY = home?.locationY?.toDouble()
		val newLocationZ = home?.locationZ?.toDouble()
		val world = home?.world?.let { Bukkit.getWorld(it) }

		return Location(world, newLocationX!!, newLocationY!!, newLocationZ!!).add(0.5, 0.0, 0.5)
	}
}