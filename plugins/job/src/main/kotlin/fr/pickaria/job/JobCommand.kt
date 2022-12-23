package fr.pickaria.job

import fr.pickaria.database.models.Job
import fr.pickaria.menu.open
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class JobCommand : CommandExecutor, TabCompleter {
	companion object {
		private val SUB_COMMANDS = listOf("ascent", "join", "leave", "menu", "top")
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			if (args.isEmpty()) {
				val message = if (sender.jobCount() == 0) {
					"§cVous n'exercez actuellement pas de métier."
				} else {
					val jobs = Job.get(sender.uniqueId).filter { it.active }.mapNotNull { Config.jobs[it.job]?.label }
					"§7Vous exercez le(s) métier(s) : ${jobs.joinToString(", ")}."
				}
				sender.sendMessage(message)
				return true
			}

			if (args[0] == "menu") {
				sender open "job"
				return true
			}

			if (args.size != 2) {
				sender.sendMessage("§cVeuillez entrer l'action et le nom du métier.")
				return false
			}

			val job = Config.jobs[args[1].lowercase()] ?: run {
				sender.sendMessage("§cCe métier n'existe pas.")
				return true
			}

			when (args[0]) {
				"join" -> {
					if (sender.jobCount() >= Config.maxJobs) {
						sender.sendMessage("§cVous ne pouvez pas avoir plus de ${Config.maxJobs} métier(s).")
					} else if (sender.hasJob(job.key)) {
						sender.sendMessage("§cVous exercez déjà ce métier.")
					} else {
						val cooldown = sender.getJobCooldown(job.key)

						if (cooldown > 0) {
							sender.sendMessage("§cVous devez attendre $cooldown minutes avant de changer de métier.")
						} else {
							sender joinJob job.key
							sender.sendMessage("§7Vous avez rejoint le métier ${job.label}.")
						}
					}
				}

				"leave" -> {
					if (!(sender hasJob job.key)) {
						sender.sendMessage("§cVous n'exercez pas ce métier.")
					} else {
						val cooldown = sender.getJobCooldown(job.key)

						if (cooldown > 0) {
							sender.sendMessage("§cVous devez attendre $cooldown minutes avant de changer de métier.")
						} else {
							sender leaveJob job.key
							sender.sendMessage("§7Vous avez quitté le métier ${job.label}.")
						}
					}
				}

				"ascent" -> {
					if (!(sender ascentJob job.key)) {
						sender.sendMessage("§7Vous ne pouvez pas effectuer une ascension pour le métier ${job.label}.")
					}
				}

				"top" -> {
					// TODO: Remake top command
				}
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String> {
		if (sender is Player) {
			return when (args.size) {
				1 -> SUB_COMMANDS.filter { it.startsWith(args[0]) }.toMutableList()

				2 -> when (args[0]) {
					"top", "join" -> Config.jobs.keys.map { it.lowercase() }.filter { it.startsWith(args[1]) }
						.toMutableList()

					"leave", "ascent" -> Job.get(sender.uniqueId)
						.filter { it.active }
						.map { it.job.lowercase() }
						.filter { it.startsWith(args[1]) }
						.toMutableList()

					else -> mutableListOf()
				}

				else -> mutableListOf()
			}
		}

		return mutableListOf()
	}
}