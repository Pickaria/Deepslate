package fr.pickaria.model.town

import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.metadata.StringDataField
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import java.util.*

private fun ByteArray.toBase64(): String = String(Base64.getEncoder().encode(this))
private fun String.fromBase64(): ByteArray = Base64.getDecoder().decode(this)

var Town.flag: ItemStack
	get() {
		val metadata = getMetadata("flag", StringDataField::class.java) ?: return ItemStack(Material.WHITE_BANNER)
		val decoded = Cbor.decodeFromByteArray<Flag>(metadata.value.fromBase64())

		return ItemStack(decoded.material).apply {
			editMeta { meta ->
				(meta as BannerMeta).patterns = decoded.patterns.map { pattern -> pattern.toPattern() }
			}
		}
	}
	set(value) {
		val patterns = (value.itemMeta as BannerMeta).patterns.map {
			SerializedPattern(it.color, it.pattern)
		}

		val flag = Cbor.encodeToByteArray(Flag(value.type, patterns))
		val flagData = StringDataField("flag", flag.toBase64())

		addMetaData(flagData)
		save()
	}