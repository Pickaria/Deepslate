package fr.pickaria.vue.job

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.job.*
import fr.pickaria.menu.open
import fr.pickaria.model.job.Job
import fr.pickaria.model.job.JobModel
import fr.pickaria.model.job.JobType
import fr.pickaria.model.job.jobConfig
import org.bukkit.entity.Player

@CommandAlias("job|jobs")
@CommandPermission("pickaria.command.job")
class JobCommand : BaseCommand() {
	companion object {
		fun setupContext(
			commandContexts: CommandContexts<BukkitCommandExecutionContext>,
			commandCompletions: CommandCompletions<BukkitCommandCompletionContext>
		) {
			commandContexts.registerContext(Job::class.java) {
				val arg: String = it.popFirstArg()

				try {
					JobType.valueOf(arg.uppercase()).toJob()
				} catch (_: IllegalArgumentException) {
					throw InvalidCommandArgument("Job of type '$arg' does not exists.")
				}
			}

			commandCompletions.registerCompletion("jobtype") {
				JobType.values().map { it.name.lowercase() }
			}

			commandCompletions.registerCompletion("ownjobs") { context ->
				JobModel.get(context.player.uniqueId)
					.filter { it.active }
					.map { it.job.lowercase() }
					.filter { it.startsWith(context.input) }
			}
		}
	}

	@Default
	fun onDefault(sender: Player) {
		val message = if (sender.jobCount() == 0) {
			throw InvalidCommandArgument("Vous n'exercez actuellement pas de métier.")
		} else {
			val jobs =
				JobModel.get(sender.uniqueId).filter { it.active }.mapNotNull { jobConfig.jobs[it.job]?.label }
			"§7Vous exercez le(s) métier(s) : ${jobs.joinToString(", ")}."
		}
		sender.sendMessage(message)
	}

	@Subcommand("menu")
	fun onMenu(sender: Player) {
		sender open "job"
	}

	@Subcommand("join")
	@Syntax("[job name]")
	@CommandCompletion("@jobtype")
	fun onJoin(sender: Player, job: Job) {
		if (sender.jobCount() >= jobConfig.maxJobs) {
			throw InvalidCommandArgument("Vous ne pouvez pas avoir plus de ${jobConfig.maxJobs} métier(s).")
		} else if (sender.hasJob(job.type)) {
			throw InvalidCommandArgument("Vous exercez déjà ce métier.")
		} else {
			val cooldown = sender.getJobCooldown(job.type)

			if (cooldown > 0) {
				throw InvalidCommandArgument("Vous devez attendre $cooldown minutes avant de changer de métier.")
			} else {
				sender joinJob job.type
				sender.sendMessage("§7Vous avez rejoint le métier ${job.label}.")
			}
		}
	}

	@Subcommand("leave")
	@Syntax("[job name]")
	@CommandCompletion("@ownjobs")
	fun onLeave(sender: Player, job: Job) {
		if (!(sender hasJob job.type)) {
			throw InvalidCommandArgument("Vous n'exercez pas ce métier.")
		} else {
			val cooldown = sender.getJobCooldown(job.type)

			if (cooldown > 0) {
				throw InvalidCommandArgument("Vous devez attendre $cooldown minutes avant de changer de métier.")
			} else {
				sender leaveJob job.type
				sender.sendMessage("§7Vous avez quitté le métier ${job.label}.")
			}
		}
	}

	@Subcommand("ascent")
	@Syntax("[job name]")
	@CommandCompletion("@ownjobs")
	fun onAscent(sender: Player, job: Job) {
		if (!(sender ascentJob job.type)) {
			throw InvalidCommandArgument("Vous ne pouvez pas effectuer une ascension pour le métier ${job.label}.")
		}
	}

	@Subcommand("top")
	@Syntax("[job name]")
	@CommandCompletion("@jobtype")
	fun onTop(sender: Player, job: Job) {
		throw InvalidCommandArgument("Currently not implemented.")
	}
}
