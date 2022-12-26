import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("com.github.johnrengelman.shadow")
	id("fr.pickaria.redstone")
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

dependencies {
	testImplementation(kotlin("test"))
	compileOnly(libs.kotlin.stdlib)
	compileOnly(libs.kotlin.reflect)
	compileOnly(libs.kotlinx.datetime)

	compileOnly(libs.paper)
	compileOnly(libs.vault)
	compileOnly(libs.acf)
	compileOnly(libs.mccoroutine.api)
	compileOnly(libs.mccoroutine.core)

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

tasks.shadowJar {
	destinationDirectory.set(file("$rootDir/server/plugins")) // Output to test server's plugins folder
}