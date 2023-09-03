package fr.pickaria.vue.reward

import fr.pickaria.controller.menu.addToHome
import fr.pickaria.controller.reward.dailyReward
import fr.pickaria.controller.reward.toInfo
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import fr.pickaria.model.reward.toController
import fr.pickaria.shared.GlowEnchantment
import kotlinx.datetime.toKotlinLocalDate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.BundleMeta
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*

private val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM")
    .withLocale(Locale.FRENCH)

fun rewardMenu() = menu("reward") {
    val playerReward = opener.dailyReward

    val remaining = playerReward.toInfo().remainingRewards
    val remainingMessage = if (remaining > 1) {
        Component.text("($remaining restantes)")
    } else {
        Component.text("($remaining restante)")
    }

    title = Component.text("Récompenses", NamedTextColor.DARK_BLUE, TextDecoration.BOLD)
        .appendSpace()
        .append(
            remainingMessage.color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
        )
    rows = 4

    val field = WeekFields.of(Locale.FRANCE).dayOfWeek()
    val today = LocalDateTime.now()
    val firstDayOfWeek = today.with(field, 1)

    for (x in 0..6) {
        val day = firstDayOfWeek.plusDays(x.toLong())
        val date = day.toLocalDate().toKotlinLocalDate()
        val dayOfWeek = formatter.format(day)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // Time info
        val endOfDay = day.with(LocalTime.MAX)
        val timeRemaining = today.until(endOfDay, ChronoUnit.HOURS)
        val unlockIn = today.until(day.with(LocalTime.MIN), ChronoUnit.HOURS)

        val info = playerReward.toInfo(date)

        item {
            position = x + 1 to 1
            title = Component.text(dayOfWeek)
            material = Material.BUNDLE
            lore {
                if (!info.isCollectionDay) {
                    description {
                        if (0 > unlockIn) {
                            -"Cette récompense ne peut plus être récupérée."
                        }
                    }
                    if (0 <= unlockIn) {
                        keyValues {
                            "Déblocage dans" to "$unlockIn heures"
                        }
                    }
                } else {
                    keyValues {
                        "Temps restants" to "$timeRemaining heures"
                        "Points collectés" to "${info.totalPoints} / ${info.pointsForNextReward}"
                        "Récompenses récupérées" to "${info.collected} / ${info.rewardCount}"
                        if (info.streakValidated) {
                            "Série" to "${info.streak} (validé)"
                        } else {
                            "Série" to info.streak
                        }
                    }
                    if (info.remainingRewards > 0) {
                        leftClick = "Clic-gauche pour récupérer la récompense"
                    }
                }
            }

            if (info.isCollectionDay) {
                leftClick = Result.CLOSE to "/reward claim"
            }

            editMeta { meta ->
                val bundle = (meta as BundleMeta)
                if (0 <= timeRemaining && info.remainingRewards > 0) {
                    info.rewards.drop(info.collected).forEach {
                        bundle.addItem(it.toController().create())
                    }
                }

                meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)

                if (info.isCollectionDay && info.canCollectRewards > 0) {
                    meta.addEnchant(GlowEnchantment.instance, 1, true)
                }
            }
        }
    }

    fill(Material.WHITE_STAINED_GLASS_PANE, true)
    closeItem()
}.addToHome(Material.SHULKER_BOX, Component.text("Récompenses")) {
    description {
        -"Obtenir des récompenses journalières."
    }
}