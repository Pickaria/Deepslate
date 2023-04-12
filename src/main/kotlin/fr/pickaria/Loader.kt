package fr.pickaria

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository


class Loader : PluginLoader {
	override fun classloader(classpathBuilder: PluginClasspathBuilder) {
		val resolver = MavenLibraryResolver()
		resolver.addRepository(
			RemoteRepository.Builder(
				"aikar",
				"default",
				"https://repo.aikar.co/content/groups/aikar/"
			).build()
		)
		resolver.addRepository(
			RemoteRepository.Builder(
				"quozul",
				"default",
				"https://maven.quozul.dev/snapshots"
			).build()
		)
		resolver.addRepository(
			RemoteRepository.Builder(
				"jitpack",
				"default",
				"https://jitpack.io"
			).build()
		)

		resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.kotlin:kotlin-reflect:1.8.20"), null))
		resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:1.8.20"), null))
		resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"), null))
		resolver.addDependency(
			Dependency(
				DefaultArtifact("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0"),
				null
			)
		)
		resolver.addDependency(Dependency(DefaultArtifact("co.aikar:acf-paper:0.5.1-SNAPSHOT"), null))
		resolver.addDependency(
			Dependency(
				DefaultArtifact("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.11.0"),
				null
			)
		)
		resolver.addDependency(
			Dependency(
				DefaultArtifact("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.11.0"),
				null
			)
		)
		resolver.addDependency(Dependency(DefaultArtifact("com.github.MilkBowl:VaultAPI:1.7.1"), null))
		resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.exposed:exposed-core:0.41.1"), null))
		resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.exposed:exposed-dao:0.41.1"), null))
		resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.exposed:exposed-jdbc:0.41.1"), null))
		resolver.addDependency(
			Dependency(
				DefaultArtifact("org.jetbrains.exposed:exposed-kotlin-datetime:0.41.1"),
				null
			)
		)
		resolver.addDependency(Dependency(DefaultArtifact("com.h2database:h2:2.1.214"), null))
		resolver.addDependency(Dependency(DefaultArtifact("fr.pickaria:bedrock:1.0.18-SNAPSHOT"), null))
		resolver.addDependency(Dependency(DefaultArtifact("com.charleskorn.kaml:kaml:0.53.0"), null))

		classpathBuilder.addLibrary(resolver);
	}
}