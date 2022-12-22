package fr.pickaria.market

import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import fr.pickaria.economy.Main
import fr.pickaria.menu.Lore
import fr.pickaria.shared.give
import fr.pickaria.spawner.event.PlayerOpenShopEvent
import net.kyori.adventure.text.Component
import net.wesjd.anvilgui.AnvilGUI
import net.wesjd.anvilgui.AnvilGUI.ResponseAction
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import kotlin.math.max


internal class AnvilCommand : CommandExecutor, Listener {
	private val plugin = JavaPlugin.getProvidingPlugin(Main::class.java)

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		return true
	}

	@EventHandler
	fun onPrepareAnvil(event: PrepareAnvilEvent) {
		val materials = event.inventory.renameText?.let { text ->
			Material.values().filter {
				text.isNotEmpty() && !it.isEmpty && it.name.lowercase().startsWith(text)
			}
		} ?: emptyList()

		for (i in 9..35) {
			val stack = materials.getOrNull(i - 9)?.let {
				val stack = ItemStack(it)

				Order.getListing(OrderType.SELL, it)?.let { order ->
					val sellPrice = max(order.averagePrice * Config.sellPercentage, Config.minimumPrice)

					val lore = Lore {
						keyValues {
							"Quantité en vente" to order.amount
							"Achat à partir de" to economy.format(order.minimumPrice)
							"Vente immédiate à" to economy.format(sellPrice)
							"Prix moyen" to economy.format(order.averagePrice)
						}
						leftClick = "Clic-gauche pour voir les options d'achat"
						rightClick = "Clic-droit pour voir les options de vente"
					}.build()

					stack.editMeta { meta ->
						meta.lore(lore)
					}
				}

				stack
			}

			event.view.bottomInventory.setItem(i, stack)
		}
	}

	@EventHandler
	fun onPlayerInteractAtEntity(event: PlayerOpenShopEvent) {
		event.player.removePotionEffect(PotionEffectType.BLINDNESS)
		val initialContent = event.player.inventory.contents

		// Clear main inventory
		event.player.inventory.apply {
			for (i in 9..35) {
				this.setItem(i, null)
			}
		}

		val stack = ItemStack(Material.PAPER).apply {
			editMeta {
				it.displayName(Component.empty())
			}
		}

		AnvilGUI.Builder()
			.onClose { player: Player ->
				player.inventory.contents = initialContent
			}
			.onComplete { completion: AnvilGUI.Completion ->
				completion.player.sendMessage(completion.text)
				if (completion.text.equals("you", ignoreCase = true)) {
					completion.player.sendMessage("You have magical powers!")
					return@onComplete listOf(ResponseAction.close())
				} else {
					return@onComplete listOf(ResponseAction.replaceInputText("Try again"))
				}
			}
			.itemLeft(stack)
			.title("Marché")
			.plugin(plugin)
			.open(event.player)

		event.isCancelled = true
	}
}