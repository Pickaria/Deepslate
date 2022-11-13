package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getProvidingPlugin(Main::class.java)
internal lateinit var economy: Economy
internal val miniMessage: MiniMessage = MiniMessage.miniMessage();
internal val currencyNamespace = NamespacedKey(plugin, "currency")
internal val valueNamespace = NamespacedKey(plugin, "value")
