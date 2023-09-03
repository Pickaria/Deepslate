package fr.pickaria.controller.job

import fr.pickaria.controller.economy.deposit
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.job.Job
import fr.pickaria.model.job.JobModel
import fr.pickaria.model.job.jobConfig
import fr.pickaria.plugin
import org.bukkit.Bukkit.getLogger
import org.bukkit.entity.Player
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.pow

private data class LastPaymentMetadata(private val value: Long, private val plugin: JavaPlugin) : MetadataValue {
    override fun value(): Any = value

    override fun asInt(): Int = value.toInt()

    override fun asFloat(): Float = value.toFloat()

    override fun asDouble(): Double = value.toDouble()

    override fun asLong(): Long = value

    override fun asShort(): Short = value.toShort()

    override fun asByte(): Byte = value.toByte()

    override fun asBoolean(): Boolean = false

    override fun asString(): String = value.toString()

    override fun getOwningPlugin(): Plugin = plugin

    override fun invalidate() = Unit
}

private var Player.lastPayment: Long
    get() {
        for (metadata in getMetadata("lastPayment")) {
            return metadata.asLong()
        }
        return 0
    }
    set(value) {
        setMetadata("lastPayment", LastPaymentMetadata(value, plugin))
    }

private fun jobPayPlayer(player: Player, amount: Double): Boolean {
    val now = System.currentTimeMillis() // This uses 32 bit, alert for future us

    if (now - player.lastPayment < jobConfig.lastPaymentDelay) {
        return false
    }
    player.lastPayment = now

    return if (player.deposit(Credit, amount).transactionSuccess()) {
        true
    } else {
        getLogger().severe("Error in payment of player ${player.name} [UUID: ${player.uniqueId}]. Amount: $amount")
        false
    }
}

fun jobPayPlayer(player: Player, amount: Double, config: Job, experienceToGive: Int = 0) {
    JobModel.get(player.uniqueId, config.type)?.let {
        val level = getLevelFromExperience(config, it.experience)
        val amountToPay = amount * config.revenueIncrease.pow(level)
        val amountIncrease = amountToPay + amountToPay * it.ascentPoints * jobConfig.ascent.moneyIncrease

        if (jobPayPlayer(player, amountIncrease) && experienceToGive > 0) {
            addExperienceAndAnnounce(player, config, experienceToGive)
        }
    }
}
