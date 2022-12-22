package fr.pickaria.shops

import fr.pickaria.Config
import fr.pickaria.economy.Credit
import fr.pickaria.economy.Key
import fr.pickaria.economy.Shard
import fr.pickaria.reforge.getAttributeItem
import fr.pickaria.reward.createReward
import org.bukkit.inventory.MerchantRecipe
import fr.pickaria.potion.Config as PotionConfig
import fr.pickaria.reward.Config as RewardConfig

fun getArtefactsOffers(): List<MerchantRecipe> =
	Config.lazyArtefacts.map { (_, artefact) ->
		val item = artefact.createReceptacle()
		val price: Int = artefact.value

		MerchantRecipe(item.clone().apply { amount = 1 }, Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Shard.item(price))
			addIngredient(Credit.item(price * 4))
		}
	}

fun getBankOffers(): List<MerchantRecipe> {
	val attributeItem = getAttributeItem()
	return listOf(
		MerchantRecipe(Key.item(), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(8, 128.0))
		},
		MerchantRecipe(Key.item(2), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(16, 128.0))
		},
		MerchantRecipe(Key.item(4), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(32, 128.0))
		},
		MerchantRecipe(Key.item(8), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(64, 128.0))
		},

		MerchantRecipe(attributeItem.asQuantity(3), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(1, 128.0))
		},
		MerchantRecipe(attributeItem.asQuantity(6), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(6, 128.0))
		},
		MerchantRecipe(attributeItem.asQuantity(9), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(9, 128.0))
		},
		MerchantRecipe(attributeItem.asQuantity(64), Int.MAX_VALUE).apply {
			uses = 0
			addIngredient(Credit.item(64, 128.0))
		},
	)
}

fun getRewardsOffers(): List<MerchantRecipe> =
	RewardConfig.rewards.filter { it.value.purchasable }.map { (key, config) ->
		createReward(key)?.let { item ->
			MerchantRecipe(item.clone().apply { amount = 1 }, Int.MAX_VALUE).apply {
				uses = 0
				addIngredient(Key.item(config.keys))
				addIngredient(Shard.item(config.shards))
			}
		}
	}.filterNotNull()

fun getPotionsOffers(): List<MerchantRecipe> = PotionConfig.potions.map { (key, config) ->
	MerchantRecipe(config.create(1).apply { amount = 1 }, Int.MAX_VALUE).apply {
		uses = 0
		addIngredient(Credit.item(5))
	}
}