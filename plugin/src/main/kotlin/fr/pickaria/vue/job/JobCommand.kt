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
		fun setupContext(manager: PaperCommandManager) {
			manager.commandContexts.registerContext(Job::class.java) {
				val arg: String = it.popFirstArg()

				try {
					JobType.valueOf(arg.uppercase()).toJob()
				} catch (_: IllegalArgumentException) {
					throw InvalidCommandArgument("Job of type '$arg' does not exists.")
				}
			}

			manager.commandCompletions.registerCompletion("jobtype") {
				JobType.values().map { it.name.lowercase() }
			}

			manager.commandCompletions.registerCompletion("ownjobs") { context ->
				JobModel.get(context.player.uniqueId)
					.filter { it.active }
					.map { it.job.name.lowercase() }
					.filter { it.startsWith(context.input) }
			}

			manager.commandConditions.addCondition("must_have_job") {
				if (it.issuer.player.jobCount() == 0) {
					throw ConditionFailedException("Vous n'exercez actuellement pas de métier.")
				}
			}
		}
	}

	@Default
	@Description("Indique les métiers que le joueur exerce actuellement.")
	@Conditions("must_have_job")
	fun onDefault(sender: Player) {
		val jobs = JobModel.get(sender.uniqueId).filter { it.active }.mapNotNull { it.job.toJob().label }
		val message = "§7Vous exercez le(s) métier(s) : ${jobs.joinToString(", ")}."
		sender.sendMessage(message)
	}

	@Subcommand("menu")
	@Description("Ouvre le menu des métiers.")
	fun onMenu(sender: Player) {
		sender open "job"
	}

	@Subcommand("join")
	@CommandCompletion("@jobtype")
	@Description("Rejoint le métier indiqué.")
	fun onJoin(sender: Player, job: Job) {
		if (sender.jobCount() >= jobConfig.maxJobs) {
			throw ConditionFailedException("Vous ne pouvez pas avoir plus de ${jobConfig.maxJobs} métier(s).")
		} else if (sender.hasJob(job.type)) {
			throw ConditionFailedException("Vous exercez déjà ce métier.")
		} else {
			val cooldown = sender.getJobCooldown(job.type)

			if (cooldown > 0) {
				throw ConditionFailedException("Vous devez attendre $cooldown minutes avant de changer de métier.")
			} else {
				sender joinJob job.type
				sender.sendMessage("§7Vous avez rejoint le métier ${job.label}.")
			}
		}
	}

	@Subcommand("leave")
	@CommandCompletion("@ownjobs")
	@Description("Quitte le métier indiqué.")
	fun onLeave(sender: Player, job: Job) {
		if (!(sender hasJob job.type)) {
			throw ConditionFailedException("Vous n'exercez pas ce métier.")
		} else {
			val cooldown = sender.getJobCooldown(job.type)

			if (cooldown > 0) {
				throw ConditionFailedException("Vous devez attendre $cooldown minutes avant de changer de métier.")
			} else {
				sender leaveJob job.type
				sender.sendMessage("§7Vous avez quitté le métier ${job.label}.")
			}
		}
	}

	@Subcommand("ascent")
	@CommandCompletion("@ownjobs")
	@Description("Réalise une ascenssion dans le métier indiqué.")
	fun onAscent(sender: Player, job: Job) {
		if (!(sender ascentJob job.type)) {
			throw ConditionFailedException("Vous ne pouvez pas effectuer une ascension pour le métier ${job.label}.")
		}
	}

	@Subcommand("top")
	@Syntax("[job name]")
	@CommandCompletion("@jobtype")
	@Description("Affiche le classement des meilleurs joueurs dans le métier indiqué.")
	fun onTop(sender: Player, job: Job) {
		throw InvalidCommandArgument("Currently not implemented.")
	}

	@HelpCommand
	fun doHelp(help: CommandHelp) {
		help.showHelp()
	}
}
