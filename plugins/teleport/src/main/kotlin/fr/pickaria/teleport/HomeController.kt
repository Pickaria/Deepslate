package fr.pickaria.teleport

import fr.pickaria.database.models.Home
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Listener
import java.util.*

class HomeController : Listener {
	fun getHomeNames(uniqueId: UUID): List<String> = Home.get(uniqueId).map { it.name }

	fun getHomeByName(uniqueId: UUID, name: String): Location? =
		Home.get(uniqueId, name)?.let {
			val world = Bukkit.getWorld(it.world)
			Location(world, it.x.toDouble(), it.y.toDouble(), it.z.toDouble())
		}

	fun removeHome(uniqueId: UUID, name: String): Boolean =
		Home.get(uniqueId, name)?.let {
			it.remove()
			true
		} ?: false

	fun addHome(uniqueId: UUID, homeName: String, location: Location) {
		Home.get(uniqueId, homeName)?.apply {
			world = location.world.uid
			x = location.x.toInt()
			y = location.y.toInt()
			z = location.z.toInt()
		} ?: run {
			Home.create(uniqueId, homeName, location.world.uid, location.x.toInt(), location.y.toInt(), location.z.toInt())
		}
	}
}