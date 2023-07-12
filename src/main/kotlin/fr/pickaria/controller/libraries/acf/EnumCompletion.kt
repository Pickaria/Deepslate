package fr.pickaria.controller.libraries.acf

import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.ConditionFailedException

inline fun <reified T : Enum<T>> BukkitCommandManager.enumCompletion(
	completionTag: String,
	exceptionMessage: String = "Invalid argument provided."
) {
	commandContexts.registerContext(T::class.java) {
		val arg: String = it.popFirstArg()

		try {
			enumValueOf<T>(arg.uppercase())
		} catch (_: IllegalArgumentException) {
			throw ConditionFailedException(exceptionMessage)
		}
	}

	commandCompletions.registerCompletion(completionTag) {
		enumValues<T>().map { it.name.lowercase() }
	}
}