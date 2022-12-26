package fr.pickaria.vue.town

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.town.TownController
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.town.BannerType
import fr.pickaria.model.town.townConfig
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BannerMeta
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.util.*

@CommandAlias("town")
@Subcommand("flag")
@Description("Permet d'obtenir une nouvelle bannière ou une copie de la bannière de sa ville.")
class BannerCommand : BaseCommand() {
	@Subcommand("buy")
	@Syntax("<color>")
	@CommandCompletion("@banner_type")
	@Description("Achète une nouvelle bannière.")
	fun onBuy(player: Player, bannerType: BannerType) {
		if (player.withdraw(Credit, townConfig.bannerPrice).transactionSuccess()) {
			player.give(bannerType.item())
		} else {
			throw ConditionFailedException("Il faut ${Credit.economy.format(townConfig.bannerPrice)} pour acheter une bannière de ville.")
		}
	}

	@Subcommand("clone")
	@Description("Créé une copie de la bannière de votre ville.")
	fun onClone(player: Player) {
		throw InvalidCommandArgument("Not implemented yet.")
	}

	private val prefixes: List<String> = listOf(
		"Elvand",
		"Aelor",
		"Arvand",
		"Erevan",
		"Némésis",
		"Thyris",
		"Galadr",
		"Cygnus",
		"Merlin",
		"Argyris",
		"Balthazar",
		"Zoltar",
		"Orphé",
		"Solar",
		"Theoph",
		"Zephyr",
		"Raz",
		"Gwyne",
		"Eudor",
	)

	private val suffixes: List<String> = listOf(
		"ar",
		"or",
		"is",
		"ielle",
		"garde",
		"ville",
		"ilus",
		"iel",
		"vere",
		"ia",
	)

	@Subcommand("random")
	@Description("Créé une bannière aléatoire.")
	@CommandPermission("pickaria.developer.randombanner")
	fun onRandom(player: Player, @Default("1") count: Int) {
		for (i in 0..count) {
			val item = BannerType.values().random().item()
			val meta = item.itemMeta as BannerMeta

			for (i in 0..5) {
				val dye = DyeColor.values().random()
				val pattern = PatternType.values().random()
				meta.addPattern(Pattern(dye, pattern))
			}

			val name = prefixes.random() + suffixes.random()
			meta.displayName(Component.text(name))

			item.itemMeta = meta
			player.give(item)

			try {
				TownController(name, item, Bukkit.getOfflinePlayer(UUID.randomUUID()))
			} catch (_: ExposedSQLException) {
				
			}
		}
	}
}