package fr.pickaria.controller.shop

import fr.pickaria.controller.economy.CurrencyBundle
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
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.random.Random

fun getArtefactsOffers(): List<MerchantRecipe> = artefactConfig.artefacts.map { (_, artefact) ->
	val item = artefact.toController().createReceptacle()

	CurrencyBundle(item) {
		Shard to artefact.value
		Credit to artefact.value * 8
	}
}

fun getBankOffers(): List<MerchantRecipe> {
	return listOf(
		CurrencyBundle(Key.toController().item()) {
			Credit to 1000
		},
		CurrencyBundle(getAttributeItem(3)) {
			Credit to 128
		},
	)
}

fun getRewardsOffers(): List<MerchantRecipe> = rewardConfig.rewards.filter { it.value.purchasable }.map { (_, reward) ->
	val item = reward.toController().create()
	CurrencyBundle(item) {
		Key to reward.keys
		Shard to reward.shards
	}
}

fun getPotionsOffers(): List<MerchantRecipe> = potionConfig.potions.map { (_, config) ->
	val item = config.toController().create()
	CurrencyBundle(item) {
		Credit to 50
	}
}

fun getTestOffers(): List<MerchantRecipe> = (0..50).map {
	CurrencyBundle(ItemStack(Material.DIRT)) {
		Credit to Random.nextDouble(0.0, 68_719_476_736.99)
	}
}

fun getFlagOffers(): List<MerchantRecipe> = BannerType.values().map {
	MerchantRecipe(it.item(), Int.MAX_VALUE).apply {
		uses = 0
		addIngredient(Credit.toController().item(10))
	}
}