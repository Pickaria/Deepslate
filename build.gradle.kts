plugins {
	kotlin("jvm") version "1.7.20"
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("fr.pickaria.redstone") version "1.2.2"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

val pickariaBedrock: String by project
val pickariaSpawner: String by project
val spigotVersion: String by project

allprojects {
	group = "fr.pickaria"
	version = "1.0-SNAPSHOT"

	apply {
		plugin("kotlin")
		plugin("com.github.johnrengelman.shadow")
		plugin("java")
	}

	tasks {
		compileKotlin {
			kotlinOptions.jvmTarget = "17"
		}
	}

	dependencies {
		compileOnly("io.papermc.paper:paper-api:$spigotVersion")
		compileOnly("fr.pickaria:bedrock:$pickariaBedrock")
		compileOnly("fr.pickaria:spawner:$pickariaSpawner")
	}

	repositories {
		mavenCentral()
		maven("https://oss.sonatype.org/content/groups/public/")
		maven("https://repo.papermc.io/repository/maven-public/")
		maven("https://maven.quozul.dev/snapshots")
		maven("https://repo.codemc.io/repository/maven-snapshots/")
	}
}

subprojects {
	// Remove Kotlin from all subprojects except 'shared'
	tasks {
		shadowJar {
			if (this@subprojects.name != "lib") {
				dependencies {
					exclude(dependency("org.jetbrains.kotlin:.*"))
				}
			}

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
}