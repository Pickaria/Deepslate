package fr.pickaria.shared

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

internal class TestCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

		val start = System.nanoTime()

		val movie = createMovie(args!![0], args[1].toInt(), args[1])
		println("${movie.id} ${movie.name}")

		val end = System.nanoTime()

		println("Took ${end - start} ns")

		return true
	}
}