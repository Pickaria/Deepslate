import groovy.json.JsonSlurper

tasks.register("setupServer") {
	val vanillaVersion = "1.19.2"
	val dependencies = mapOf(
		"PlugManX" to 88135,
		"Vault" to 34315,
		"ProtocolLib" to 1997,
	)

	fun download(url: String, path: String): File {
		val destFile = File(path)
		ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
		return destFile
	}

	fun json(url: String): Map<*, *> {
		val file = download(url, "$rootDir/server/.temp")
		val j = JsonSlurper().parseText(file.readText()) as Map<*, *>
		file.delete()
		return j
	}

	doLast {
		println("Creating folder structure...")
		mkdir("$rootDir/server")
		mkdir("$rootDir/server/plugins")

		// Download Paper
		println("Fetching paper version...")
		val response = json("https://papermc.io/api/v2/projects/paper/versions/${vanillaVersion}")
		val build = (response["builds"] as List<*>).last()

		val json = json("https://papermc.io/api/v2/projects/paper/versions/${vanillaVersion}/builds/${build}")
		val server = ((json["downloads"] as Map<*, *>)["application"] as Map<*, *>)["name"] as String

		println("Downloading $server...")
		download(
			"https://papermc.io/api/v2/projects/paper/versions/${vanillaVersion}/builds/${build}/downloads/${server}",
			"$rootDir/server/paper.jar"
		)

		// Download dependencies
		for ((name, id) in dependencies) {
			println("Downloading $name...")
			download(
				"https://api.spiget.org/v2/resources/$id/download",
				"$rootDir/server/plugins/$name.jar"
			)
		}

		// Accept EULA
		println("Accepting EULA...")
		val eula = file("$rootDir/server/eula.txt")
		eula.createNewFile()
		eula.writeText("eula=true")

		println("Server ready!")
	}
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
	}

	repositories {
		mavenCentral()
		maven("https://oss.sonatype.org/content/groups/public/")
		maven("https://repo.papermc.io/repository/maven-public/")
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