package fr.pickaria.economy

import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getLogger
import org.bukkit.Bukkit.getServer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PayCommand : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val recipient = try {
				getServer().getOfflinePlayer(args[0])
			} catch (_: ArrayIndexOutOfBoundsException) {
				return false
			}

			if (recipient == sender) {
				sender.sendMessage("§cVous ne pouvez pas envoyer de l'argent à vous-même.")
				return true
			}

			if (!recipient.isOnline && !recipient.hasPlayedBefore()) {
				sender.sendMessage("§cCe joueur n'est jamais venu sur le serveur.")
				return true
			}

			val amount = try {
				args[1].toDouble()
			} catch (_: NumberFormatException) {
				sender.sendMessage("§cLa valeur que vous avez entrée n'est pas un nombre.")
				return true
			} catch (_: ArrayIndexOutOfBoundsException) {
				return false
			}

			if (amount <= 0.01) {
				sender.sendMessage("§cLe montant doit être supérieur à 0.01.")
				return true
			}

			if (economy.has(sender, amount)) {
				val withdrawResponse = economy.withdrawPlayer(sender, amount)

				if (withdrawResponse.type == EconomyResponse.ResponseType.SUCCESS) {
					val depositResponse = economy.depositPlayer(recipient, withdrawResponse.amount)

					if (depositResponse.type != EconomyResponse.ResponseType.SUCCESS) {
						sender.sendMessage("§cLe destinataire n'a pas pu recevoir l'argent.")

						// Try to refund
						val refund = economy.depositPlayer(sender, withdrawResponse.amount)
						if (refund.type == EconomyResponse.ResponseType.FAILURE) {
							getLogger().severe("Can't refund player, withdrew amount: ${withdrawResponse.amount}")
							sender.sendMessage("§4Une erreur est survenue lors du remboursement, contactez un administrateur.")
						}
					} else {
						val format = economy.format(depositResponse.amount)
						sender.sendMessage("§7Le destinataire a bien reçu §6${format}§7.")

						if (recipient.isOnline) {
							(recipient as Player).sendMessage("§6${sender.name} §7vous a envoyé §6${format}§7.")
						}
					}
				} else {
					sender.sendMessage("§cUne erreur est survenue lors de la transaction.")
				}
			} else {
				sender.sendMessage("§cVous n'avez pas assez d'argent.")
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
		return if (args.size == 1) {
			getServer().onlinePlayers.filter {
				it.name.startsWith(args[0]) && sender !== it
			}.map {
				it.name
			}.toMutableList()
		} else {
			mutableListOf()
		}
	}
}