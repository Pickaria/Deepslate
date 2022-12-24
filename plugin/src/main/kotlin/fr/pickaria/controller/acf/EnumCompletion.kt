package fr.pickaria.controller.acf

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager

inline fun <reified T : Enum<T>> enumCompletion(
	manager: PaperCommandManager,
	completionTag: String,
	exceptionMessage: String = "Invalid argument provided."
) {
	manager.commandContexts.registerContext(T::class.java) {
		val arg: String = it.popFirstArg()

		try {
			enumValueOf<T>(arg.uppercase())
		} catch (_: IllegalArgumentException) {
			throw ConditionFailedException(exceptionMessage)
		}
	}

	manager.commandCompletions.registerCompletion(completionTag) {
		enumValues<T>().map { it.name.lowercase() }
	}
}