package fr.pickaria.model.economy

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.datasources.getResourceFileStream
import fr.pickaria.model.serializers.MiniMessageSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class EconomyConfig(
	val currencies: Map<String, Currency>,

	@SerialName("balance_message")
	val balanceMessage: String,

	@SerialName("cant_send_to_yourself")
	val cantSendToYourself: String,

	@Serializable(with = MiniMessageSerializable::class)
	@SerialName("player_does_not_exist")
	val playerDoesNotExist: Component,

	@Serializable(with = MiniMessageSerializable::class)
	@SerialName("amount_is_nan")
	val amountIsNan: Component,

	@SerialName("less_than_minimum_amount")
	val lessThanMinimumAmount: String,

	@Serializable(with = MiniMessageSerializable::class)
	@SerialName("receive_error")
	val receiveError: Component,

	@Serializable(with = MiniMessageSerializable::class)
	@SerialName("refund_error")
	val refundError: Component,

	@Serializable(with = MiniMessageSerializable::class)
	@SerialName("send_error")
	val sendError: Component,

	@Serializable(with = MiniMessageSerializable::class)
	@SerialName("not_enough_money")
	val notEnoughMoney: Component,

	@SerialName("not_much_pages")
	val notMuchPages: String,

	@SerialName("send_success")
	val sendSuccess: String,

	@SerialName("receive_success")
	val receiveSuccess: String,

	val header: String,
	val row: String,
	val footer: String,
)

val economyConfig = Yaml.default.decodeFromStream<EconomyConfig>(getResourceFileStream("economy.yml"))
