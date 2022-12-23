package fr.pickaria.model.potion

enum class PotionType {
	JOB_EXPERIENCE;

	fun toPotion(): Potion = potionConfig.potions[name.lowercase()]!!
}