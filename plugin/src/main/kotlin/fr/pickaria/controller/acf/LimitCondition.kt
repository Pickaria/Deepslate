package fr.pickaria.controller.acf

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager

fun limitCondition(manager: PaperCommandManager) {
	manager.commandConditions.addCondition(
		Int::class.java,
		"limits"
	) { context, _, value ->
		if (value == null) {
			return@addCondition
		}
		if (context.hasConfig("min") && context.getConfigValue("min", 0) > value) {
			val min = context.getConfigValue("min", 0)
			throw ConditionFailedException("La valeur doit être supérieure à $min.")
		}
		if (context.hasConfig("max") && context.getConfigValue("max", 3) < value) {
			val max = context.getConfigValue("max", 3)
			throw ConditionFailedException("La valeur doit être inférieure à $max.")
		}
	}
}