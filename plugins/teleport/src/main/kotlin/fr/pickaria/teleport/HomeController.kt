package fr.pickaria.teleport

import fr.pickaria.shared.models.Home
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Listener
import java.util.*

class HomeController : Listener {
	fun getHomeNames(uniqueId: UUID): List<String> = Home.get(uniqueId).map { it.name }

	fun getHomeByName(uniqueId: UUID, name: String): Location? {
		val home = Home.get(uniqueId, name)

		return if (home != null) {
			val world = try {
				Bukkit.getWorld(home.world)
			} catch (_: NullPointerException) {
				null
			}

			return Location(world, home.x.toDouble(), home.y.toDouble(), home.z.toDouble())
		} else {
			null
		}
	}

	fun removeHome(uniqueId: UUID, name: String): Boolean =
		Home.get(uniqueId, name)?.let {
			it.remove()
			true
		} ?: false

	fun addHome(uniqueId: UUID, homeName: String, location: Location) {
		Home.create(uniqueId, homeName, location.world.uid, location.x.toInt(), location.y.toInt(), location.z.toInt())
	}
}