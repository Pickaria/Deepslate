package fr.pickaria.model.reward

import kotlinx.datetime.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.security.InvalidParameterException

@Serializable(with = MonthDaySerializer::class)
class MonthDay(val month: Month, val dayOfMonth: Int) {
	companion object {
		fun now(): MonthDay {
			val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
			return MonthDay(today.month, today.dayOfMonth)
		}
	}

	init {
		if (dayOfMonth > month.maxLength()) {
			throw InvalidParameterException("Day provided is invalid for month $month.")
		}
	}

	override operator fun equals(other: Any?): Boolean {
		return if (other is MonthDay) {
			other.month == month && other.dayOfMonth == dayOfMonth
		} else {
			false
		}
	}

	override fun hashCode(): Int {
		return month.number * 31 + dayOfMonth
	}
}

object MonthDaySerializer : KSerializer<MonthDay> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MonthDay", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): MonthDay {
		val (month, dayOfMonth) = decoder.decodeString().split('-', limit = 2).map {
			it.toInt()
		}
		return MonthDay(Month.of(month), dayOfMonth)
	}

	override fun serialize(encoder: Encoder, value: MonthDay) {
		encoder.encodeString("${value.month}-${value.dayOfMonth}")
	}
}

fun LocalDate.toMonthDay(): MonthDay = MonthDay(month, dayOfMonth)