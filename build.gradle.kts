import fr.pickaria.redstone.SetupServer

buildscript {
	repositories {
		maven {
			url = uri("https://maven.quozul.dev/snapshots")
		}
	}
	dependencies {
		classpath("fr.pickaria:redstone:1.1-SNAPSHOT")
	}
}

tasks.register<SetupServer>("setupServer") {
	vanillaVersion = "1.19.3"
	directory = "$rootDir/server"
	dependencies = mapOf(
		"_PlugManX" to 88135,
		"_Vault" to 34315,
		"_autoreload" to 50888,
	)
}

tasks.register<JavaExec>("startServer") {
	workingDir = File("$rootDir/server")
	classpath = files("$workingDir/server.jar")
	jvmArgs = listOf("-Xmx2G")
	args = listOf("nogui")
	standardInput = System.`in`
}

plugins {
	kotlin("jvm") version "1.7.20"
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

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
		compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
		compileOnly("fr.pickaria:bedrock:1.0.2-SNAPSHOT")
	}

	repositories {
		mavenCentral()
		maven("https://oss.sonatype.org/content/groups/public/")
		maven("https://repo.papermc.io/repository/maven-public/")
		maven("https://maven.quozul.dev/snapshots")
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