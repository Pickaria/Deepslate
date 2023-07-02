package fr.pickaria.controller.libraries.datetime

import kotlinx.datetime.toDateTimePeriod
import kotlin.time.Duration

private fun plural(amount: Int) = if (amount > 1) "s" else ""

fun Duration.autoFormat(): String {
	val period = toDateTimePeriod()
	val string = StringBuilder()

	if (period.years > 0) {
		string.append("${period.years} année${plural(period.years)} ")
	}
	if (period.months > 0) {
		string.append("${period.months} mois ")
	}
	if (period.days > 0) {
		string.append("${period.days} jour${plural(period.days)} ")
	}
	if (period.hours > 0) {
		string.append("${period.hours} heure${plural(period.hours)} ")
	}
	if (period.minutes > 0) {
		string.append("${period.minutes} minute${plural(period.minutes)} ")
	}
	if (period.seconds > 0) {
		string.append("${period.seconds} seconde${plural(period.seconds)} ")
	}

	return string.trim().toString()
}