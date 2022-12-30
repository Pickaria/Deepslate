import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	id("com.github.johnrengelman.shadow")
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

dependencies {
	testImplementation(kotlin("test"))
	implementation(libs.kotlin.reflect)
	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlinx.datetime)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.serialization.cbor)

	compileOnly(libs.paper)
	implementation(libs.acf)
	implementation(libs.mccoroutine.api)
	implementation(libs.mccoroutine.core)

	implementation(libs.exposed.core)
	implementation(libs.exposed.dao)
	implementation(libs.exposed.jdbc)
	implementation(libs.exposed.kotlin.datetime)
	implementation(libs.h2)

	implementation(libs.pickaria.bedrock)

	implementation(libs.kaml)
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "17"
}

tasks.shadowJar {
	destinationDirectory.set(file("$rootDir/server/plugins")) // Output to test server's plugins folder
}
