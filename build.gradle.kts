import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	kotlin("jvm") version "1.7.20" // or kotlin("multiplatform") or any other kotlin plugin
	id("com.github.johnrengelman.shadow") version "7.1.2"
	java
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

allprojects {
	group = "fr.pickaria"
	version = "1.0-SNAPSHOT"

	repositories {
		mavenCentral()
		maven("https://oss.sonatype.org/content/groups/public/")
		maven("https://repo.papermc.io/repository/maven-public/")
	}
}

subprojects {
	apply {
		plugin("kotlin")
		plugin("com.github.johnrengelman.shadow")
	}

	dependencies {
		implementation(kotlin("stdlib"))
		compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}

	tasks.withType<ShadowJar> {
		mergeServiceFiles()

		val dest: String = when (System.getenv("DESTINATION_DIRECTORY")) {
			"build" -> {
				"$rootDir/build"
			}
			else -> {
				"$rootDir/server/plugins"
			}
		}

		destinationDirectory.set(file(dest)) // Output to test server's plugins folder
	}
}