package fr.pickaria.menu

import fr.pickaria.menu.sub.HomeMenu
import kotlin.reflect.KClass

enum class MenuEnum(val title: String, val menu: KClass<*>) {
	EMPTY("§6§lTest vide", BaseMenu::class),
	HOME("§6§lAccueil", HomeMenu::class);
}