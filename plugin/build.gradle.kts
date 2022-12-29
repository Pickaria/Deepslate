import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("com.github.johnrengelman.shadow")
	id("fr.pickaria.redstone")
	id("com.google.devtools.ksp") version "1.7.22-1.0.8"
	id("io.gitlab.arturbosch.detekt").version("1.22.0")
}

detekt {
	toolVersion = "1.22.0"
	config = files("${rootDir}/detekt.yml")
	buildUponDefaultConfig = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
	reports {
		xml.required.set(true)
		html.required.set(true)
		txt.required.set(true)
		sarif.required.set(true)
		md.required.set(true)
	}
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

buildscript {
	dependencies {
		classpath(kotlin("gradle-plugin", version = "1.7.22"))
	}
}

dependencies {
	implementation(project(":processor"))
	ksp(project(":processor"))

	compileOnly(libs.kotlin.stdlib)
	compileOnly(libs.kotlin.reflect)
	compileOnly(libs.kotlinx.datetime)
	compileOnly(libs.kotlinx.serialization.json)

	compileOnly(libs.paper)
	compileOnly(libs.vault)
	compileOnly(libs.acf)
	compileOnly(libs.mccoroutine.api)
	compileOnly(libs.mccoroutine.core)
	compileOnly(libs.towny)

	compileOnly(libs.exposed.core)
	compileOnly(libs.exposed.dao)
	compileOnly(libs.exposed.jdbc)
	compileOnly(libs.exposed.kotlin.datetime)
	compileOnly(libs.h2)

	compileOnly(libs.pickaria.spawner)
	compileOnly(libs.pickaria.bedrock)

	compileOnly(libs.kaml)
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "17"
}

tasks {
	shadowJar {
		destinationDirectory.set(file("$rootDir/server/plugins")) // Output to test server's plugins folder
	}

	compileKotlin {
		kotlinOptions.javaParameters = true
	}
}
