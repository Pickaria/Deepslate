package fr.pickaria.shard

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import org.apache.log4j.BasicConfigurator
import org.bukkit.Material
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class PlaceShopCommandTest {
	private lateinit var server: ServerMock
	private lateinit var plugin: Main

	@BeforeEach
	fun setUp() {
		BasicConfigurator.configure()
		server = MockBukkit.mock()
		plugin = MockBukkit.load(Main::class.java)
	}

	@AfterEach
	fun tearDown() {
		MockBukkit.unmock()
	}

	@Test
	fun onCommand() {
		val world = WorldMock(Material.DIRT, 3)
		val player = server.addPlayer()
	}
}