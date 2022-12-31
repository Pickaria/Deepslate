package fr.pickaria.model.town

import fr.pickaria.model.config
import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class TownConfig(
	@SerialName("banner_price")
	val bannerPrice: Double,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("town_name_missing")
	val townNameMissing: Component,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("town_name_exist")
	val townNameExist: Component,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("already_in_town")
	val alreadyInTown: Component,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("town_not_created")
	val townNotCreated: Component,

	@SerialName("town_empty_deleted")
	val townEmptyDeleted: String,

	val page: String,

	val header: String,

	@SerialName("town_row")
	val townRow: String,

	@SerialName("resident_row")
	val residentRow: String,

	@SerialName("town_footer")
	val townFooter: String,

	@SerialName("resident_footer")
	val residentFooter: String,

	@SerialName("town_info")
	val townInfo: String,

	@SerialName("withdraw_success")
	val withdrawSuccess: String,

	@SerialName("deposit_success")
	val depositSuccess: String,

	@SerialName("joined_town")
	val joinedTown: String,

	@SerialName("left_town")
	val leftTown: String,

	@SerialName("not_in_town")
	val notInTown: String,

	@SerialName("invite_received")
	val inviteReceived: String,

	@SerialName("invite_sent")
	val inviteSent: String,

	@SerialName("no_permission")
	val noPermission: String,

	val kicked: String,

	@SerialName("kick_success")
	val kickSuccess: String,
)

val townConfig = config<TownConfig>("town.yml")
