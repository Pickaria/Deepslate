package fr.pickaria.vue.chat

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode
import com.xxmicloxx.NoteBlockAPI.model.Song
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder
import fr.pickaria.model.chat.chatConfig
import fr.pickaria.model.mainConfig
import fr.pickaria.shared.updateDisplayName
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

internal class PlayerJoin(val plugin: JavaPlugin) : Listener {
    private val song: Song =
        NBSDecoder.parse(File(plugin.dataFolder, "songs/Never Gonna Give You Up.nbs"))
    private val songPlayer = PositionSongPlayer(song)

    init {
        val location = Location(mainConfig.lobbyWorld, 26.0, 61.0, -24.0)
        songPlayer.setTargetLocation(location)
        songPlayer.setDistance(32)
        songPlayer.isPlaying = true
        songPlayer.isRandom = true
        songPlayer.repeatMode = RepeatMode.ALL
        songPlayer.volume = 20
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        with(event) {
            player.updateDisplayName()
            songPlayer.addPlayer(player)

            if (!player.hasPermission("pickaria.messages.join") && !player.hasPlayedBefore()) {
                joinMessage(null)
                // TODO: Log join message to admin players
            } else {
                joinMessage(chatConfig.join.append(player.displayName()))
            }

            player.updateTabHeaderAndFooter()
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        with(event) {
            player.updateDisplayName()

            this@PlayerJoin.songPlayer.removePlayer(player)

            if (!player.hasPermission("pickaria.messages.quit")) {
                quitMessage(null)
                // TODO: Log join message to admin players
            } else {
                quitMessage(chatConfig.quit.append(player.displayName()))
            }
        }
    }

    @EventHandler
    fun onPlayer(event: PlayerAdvancementDoneEvent) {
        with(event) {
            player.updateDisplayName()

            if (!player.hasPermission("pickaria.messages.advancement")) {
                message(null)
            }
        }
    }
}