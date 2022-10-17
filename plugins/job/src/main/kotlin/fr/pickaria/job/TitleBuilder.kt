package fr.pickaria.job

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import java.time.Duration

class TitleBuilder() {
	private var mainTitle: TextComponent = Component.text("Main title")
	private var subTitle: TextComponent = Component.text("Sub title")
	private var time: Title.Times? = null

	private constructor(options: TitleBuilderOptions): this(){
		options.mainTitle?.let { mainTitle = it }
		options.subTitle?.let { subTitle = it }
		options.time?.let { time = it }
	}

	fun mainTitle(text: String, color: NamedTextColor? = null): TitleBuilder{
		mainTitle = color?.let { Component.text(text, color)} ?: Component.text(text)
		return this
	}

	fun subTitle(text: String, color: NamedTextColor? = null): TitleBuilder{
		subTitle = color?.let { Component.text(text, color) } ?: Component.text(text)
		return this
	}

	fun time(fadeIn: Duration = Duration.ofMillis(250), stay: Duration = Duration.ofSeconds(2), fadeOut: Duration = Duration.ofMillis(250)): TitleBuilder{
		time = Title.Times.times(fadeIn, stay, fadeOut)
		return this
	}

	fun build(): Title = Title.title(mainTitle, subTitle, time)

	companion object {

		class TitleBuilderOptions{
			var mainTitle: TextComponent? = null
			var subTitle: TextComponent? = null
			var time: Title.Times? = null
		}
		operator fun invoke(builder: TitleBuilderOptions.() -> Unit) = TitleBuilder(TitleBuilderOptions().apply(builder))
	}
}