package fr.pickaria.market

import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import fr.pickaria.economy.Main
import fr.pickaria.menu.Lore
import fr.pickaria.spawner.spawnVillager
import net.kyori.adventure.text.Component
import net.wesjd.anvilgui.AnvilGUI
import net.wesjd.anvilgui.AnvilGUI.ResponseAction
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.max


internal class AnvilCommand : CommandExecutor, Listener {
	private val plugin = JavaPlugin.getProvidingPlugin(Main::class.java)
	private val namespace = NamespacedKey(plugin, "market")

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val villager = spawnVillager(sender.location, "Marché")
			villager.persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)
			villager.villagerType = Villager.Type.TAIGA
			villager.profession = Villager.Profession.NITWIT
			villager.resetOffers()
			villager.customName(Component.text("Dumbbell"))
			villager.isCustomNameVisible = true
		}

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
	fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
		if (!event.rightClicked.persistentDataContainer.has(namespace)) return
		val initialContent = event.player.inventory.contents

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

		// Clear main inventory
		event.player.inventory.apply {
			for (i in 9..35) {
				this.setItem(i, null)
			}
		}

		event.isCancelled = true
	}
}