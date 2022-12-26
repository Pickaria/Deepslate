package fr.pickaria.controller.shop

import fr.pickaria.controller.reforge.getAttributeItem
import fr.pickaria.model.artefact.artefactConfig
import fr.pickaria.model.artefact.toController
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.Key
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.economy.toController
import fr.pickaria.model.potion.potionConfig
import fr.pickaria.model.potion.toController
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toController
import fr.pickaria.model.town.BannerType
import org.bukkit.inventory.MerchantRecipe

fun getArtefactsOffers(): List<MerchantRecipe> =
	artefactConfig.artefacts.map { (_, artefact) ->
		val item = artefact.toController().createReceptacle()
		val price: Int = artefact.value

		MerchantRecipe(item.clone().apply { amount = 1 }, Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Shard.toController().item(price))
			addIngredient(Credit.toController().item(price * 4))
		}
	}

fun getBankOffers(): List<MerchantRecipe> {
	val attributeItem = getAttributeItem()
	val keyController = Key.toController()
	val creditController = Credit.toController()

	return listOf(
		MerchantRecipe(keyController.item(), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(8, 128.0))
		},
		MerchantRecipe(attributeItem.asQuantity(3), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(1, 128.0))
		},
	)
}

fun getRewardsOffers(): List<MerchantRecipe> =
	rewardConfig.rewards.filter { it.value.purchasable }.map { (_, reward) ->
		val item = reward.toController().create()
		MerchantRecipe(item.clone().apply { amount = 1 }, Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Key.toController().item(reward.keys))
			addIngredient(Shard.toController().item(reward.shards))
		}
	}

fun getPotionsOffers(): List<MerchantRecipe> = potionConfig.potions.map { (_, config) ->
	MerchantRecipe(config.toController().create().apply { amount = 1 }, Int.MAX_VALUE).apply {
		uses = 0
		addIngredient(Credit.toController().item(5))
	}
}

fun getFlagOffers(): List<MerchantRecipe> = BannerType.values().map {
	MerchantRecipe(it.item(), Int.MAX_VALUE).apply {
		uses = 0
		addIngredient(Credit.toController().item(10))
	}
}