pluginManagement {
	repositories {
		maven("https://maven.quozul.dev/snapshots")
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		// Update this in libs.version.toml when you change it here
		kotlin("jvm") version "1.7.22"
		kotlin("plugin.serialization") version "1.7.22"
		id("com.github.johnrengelman.shadow") version "7.1.2"
		id("fr.pickaria.redstone") version "1.2.3"
	}
}

rootProject.name = "deepslate"

include("plugin", "libraries")

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("libs.versions.toml"))
		}
	}

	repositories {
		mavenCentral()
		maven("https://oss.sonatype.org/content/groups/public/")
		maven("https://repo.papermc.io/repository/maven-public/") // Paper
		maven("https://maven.quozul.dev/snapshots")
		maven("https://repo.aikar.co/content/groups/aikar/") // ACF
		maven("https://jitpack.io") // Vault
	}
}
