package fr.pickaria.vue.town

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.town.createTownBanner
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.town.BannerType
import fr.pickaria.model.town.Town
import fr.pickaria.model.town.townConfig
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import org.bukkit.DyeColor
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.jetbrains.exposed.sql.transactions.transaction

@CommandAlias("banner")
@CommandPermission("pickaria.command.banner")
@Description("Permet d'obtenir une nouvelle bannière ou une copie de la bannière de sa ville.")
class BannerCommand : BaseCommand() {
	@HelpCommand
	@Syntax("")
	fun doHelp(help: CommandHelp) {
		help.showHelp()
	}

	@Subcommand("buy")
	@Syntax("<color>")
	@CommandCompletion("@banner_type")
	@Description("Achète une nouvelle bannière.")
	fun onBuy(player: Player, bannerType: BannerType) {
		if (player.withdraw(Credit, townConfig.bannerPrice).transactionSuccess()) {
			player.give(createTownBanner(bannerType))
		} else {
			throw ConditionFailedException("Il faut ${Credit.economy.format(townConfig.bannerPrice)} pour acheter une bannière de ville.")
		}
	}

	@Subcommand("clone")
	@Description("Créé une copie de la bannière de votre ville.")
	fun onClone(player: Player) {
		throw InvalidCommandArgument("Not implemented yet.")
	}

	@Subcommand("random")
	@Description("Créé une bannière aléatoire.")
	@CommandPermission("pickaria.developer.randombanner")
	fun onRandom(player: Player) {
		var max = 0
		for (x in 0..9) {
			val item = ItemStack(BannerType.values().random().material)
			val meta = item.itemMeta as BannerMeta

			for (i in 0..5) {
				val dye = DyeColor.values().random()
				val pattern = PatternType.values().random()
				meta.addPattern(Pattern(dye, pattern))
			}

			meta.displayName(Component.text("abc"))

			item.itemMeta = meta
			player.give(item)

			val size = item.serializeAsBytes().size

			if (size > max) {
				max = size
			}

			transaction {
				Town.new {
					identifier = "abc"
					flag = item
				}
			}
		}
		player.sendMessage("Biggest serialized banner size: $max bytes")
	}
}