import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import java.io.OutputStream

class TestProcessor(
	val codeGenerator: CodeGenerator, val options: Map<String, String>
) : SymbolProcessor {
	operator fun OutputStream.plusAssign(str: String) {
		this.write(str.toByteArray())
	}

	override fun process(resolver: Resolver): List<KSAnnotated> {
		val symbols = resolver.getSymbolsWithAnnotation("co.aikar.commands.annotation.CommandAlias")
		if (!symbols.iterator().hasNext()) return emptyList()

		val file = codeGenerator.createNewFile(
			dependencies = Dependencies(false),
			packageName = "",
			fileName = "commands",
			extensionName = "md",
		)

		file += "# Commandes\n\n"

		symbols.forEach { symbol ->
			symbol.annotations.forEach {
				when (it.shortName.asString()) {
					"CommandAlias" -> {
						file += "## `/${it.arguments.first().toString().replace("value:", "")}`\n"
					}

					"CommandPermission" -> {
						file += "Permission : `" + it.arguments.first().toString().replace("value:", "") + "`\n"
					}

					"Description" -> {
						file += "> " + it.arguments.first().toString().replace("value:", "") + "\n"
					}
				}
			}
			file += "\n"
		}

		file.close()

		val unableToProcess = symbols.filterNot { it.validate() }.toList()
		return unableToProcess
	}
}

class TestProcessorProvider : SymbolProcessorProvider {
	override fun create(
		environment: SymbolProcessorEnvironment
	): SymbolProcessor {
		return TestProcessor(environment.codeGenerator, environment.options)
	}
}
