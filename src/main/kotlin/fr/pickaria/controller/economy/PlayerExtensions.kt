package fr.pickaria.controller.economy

import fr.pickaria.model.economy.Currency
import fr.pickaria.model.economy.SendResponse
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getLogger
import org.bukkit.OfflinePlayer

fun OfflinePlayer.balance(currency: Currency): Double = currency.economy.getBalance(this)

fun OfflinePlayer.has(currency: Currency, amount: Double): Boolean = currency.economy.has(this, amount)

fun OfflinePlayer.withdraw(currency: Currency, amount: Double): EconomyResponse {
    val response = currency.economy.withdrawPlayer(this, amount)

    if (response.transactionSuccess()) {
        this.player?.let {
            it.playSound(currency.collectSound)
            it.sendActionBar(Component.text("- ${currency.economy.format(amount)}", NamedTextColor.RED))
        }
    }

    return response
}

fun OfflinePlayer.deposit(currency: Currency, amount: Double): EconomyResponse {
    val response = currency.economy.depositPlayer(this, amount)

    if (response.transactionSuccess()) {
        this.player?.let {
            it.playSound(currency.collectSound)
            it.sendActionBar(Component.text("+ ${currency.economy.format(amount)}", NamedTextColor.GOLD))
        }
    }

    return response
}

fun OfflinePlayer.has(currency: Currency, amount: Int): Boolean = has(currency, amount.toDouble())

fun OfflinePlayer.withdraw(currency: Currency, amount: Int): EconomyResponse = withdraw(currency, amount.toDouble())

fun OfflinePlayer.deposit(currency: Currency, amount: Int): EconomyResponse = deposit(currency, amount.toDouble())

fun sendTo(currency: Currency, sender: OfflinePlayer, recipient: OfflinePlayer, amount: Double): SendResponse =
    if (sender.has(currency, amount)) {
        val withdrawResponse = sender.withdraw(currency, amount)

        if (withdrawResponse.type == EconomyResponse.ResponseType.SUCCESS) {
            val depositResponse = recipient.deposit(currency, amount)

            if (depositResponse.type != EconomyResponse.ResponseType.SUCCESS) {
                // Try to refund
                val refund = sender.deposit(currency, amount)
                if (refund.type == EconomyResponse.ResponseType.FAILURE) {
                    getLogger().severe("Can't refund player, withdrew amount: $amount")
                    SendResponse.REFUND_ERROR
                }

                SendResponse.RECEIVE_ERROR
            } else {
                SendResponse.SUCCESS
            }
        } else {
            SendResponse.SEND_ERROR
        }
    } else {
        SendResponse.NOT_ENOUGH_MONEY
    }