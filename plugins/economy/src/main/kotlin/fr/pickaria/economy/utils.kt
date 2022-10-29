package fr.pickaria.economy

import org.bukkit.entity.Player

internal infix fun Player.has(amount: Double): Boolean = economy.has(this@has, amount)
