package fr.pickaria.controller.shop

import fr.pickaria.controller.economy.CurrencyBundle
import fr.pickaria.controller.reforge.getAttributeItem
import fr.pickaria.controller.town.TownHallRegister
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
		CurrencyBundle(Key.toController().item(1.0)) {
			Credit to 10_000
		},
		CurrencyBundle(getAttributeItem(3)) {
			Credit to 3_000
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
		Credit to 10_000
	}
}

fun getTestOffers(): List<MerchantRecipe> = (0..50).map {
	CurrencyBundle(ItemStack(Material.DIRT)) {
		Credit to Random.nextDouble(0.0, 68_719_476_736.99)
	}
}

fun getFlagOffers(): List<MerchantRecipe> =
	listOf(CurrencyBundle(TownHallRegister.create()) {
		Credit to 250
	}) + BannerType.values().map {
		CurrencyBundle(it.item()) {
			Credit to 250
		}
	}