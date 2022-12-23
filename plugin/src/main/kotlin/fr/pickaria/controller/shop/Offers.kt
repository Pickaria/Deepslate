package fr.pickaria.controller.shop

import fr.pickaria.controller.reforge.getAttributeItem
import fr.pickaria.model.artefact.artefactConfig
import fr.pickaria.model.artefact.toController
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.Key
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.economy.toController
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
		MerchantRecipe(keyController.item(2), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(16, 128.0))
		},
		MerchantRecipe(keyController.item(4), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(32, 128.0))
		},
		MerchantRecipe(keyController.item(8), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(64, 128.0))
		},

		MerchantRecipe(attributeItem.asQuantity(3), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(1, 128.0))
		},
		MerchantRecipe(attributeItem.asQuantity(6), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(6, 128.0))
		},
		MerchantRecipe(attributeItem.asQuantity(9), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(9, 128.0))
		},
		MerchantRecipe(attributeItem.asQuantity(64), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(creditController.item(64, 128.0))
		},
	)
}

/*fun getRewardsOffers(): List<MerchantRecipe> =
	rewardConfig.rewards.filter { it.value.purchasable }.map { (key, config) ->
		createReward(key)?.let { item ->
			MerchantRecipe(item.clone().apply { amount = 1 }, Int.MAX_VALUE).apply {
				uses = 0
				addIngredient(keyController.item(config.keys))
				addIngredient(Shard.item(config.shards))
			}
		}
	}.filterNotNull()

fun getPotionsOffers(): List<MerchantRecipe> = PotionConfig.potions.map { (key, config) ->
	MerchantRecipe(config.create(1).apply { amount = 1 }, Int.MAX_VALUE).apply {
		uses = 0
		addIngredient(creditController.item(5))
	}
}*/