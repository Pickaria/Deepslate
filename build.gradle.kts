import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java")
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("com.github.johnrengelman.shadow")
	id("fr.pickaria.redstone")
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

dependencies {
	testImplementation(kotlin("test"))
	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlin.reflect)
	implementation(libs.kotlinx.datetime)
	implementation(libs.kotlinx.serialization.json)

	compileOnly(libs.paper)
	compileOnly(libs.vault)
	implementation(libs.acf)
	implementation(libs.mccoroutine.api)
	implementation(libs.mccoroutine.core)
	compileOnly(libs.towny)

	implementation(libs.exposed.core)
	implementation(libs.exposed.dao)
	implementation(libs.exposed.jdbc)
	implementation(libs.exposed.kotlin.datetime)
	implementation(libs.h2)

	compileOnly(libs.pickaria.spawner)
	implementation(libs.pickaria.bedrock)

	implementation(libs.kaml)
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

val targetJavaVersion = 17
java {
	val javaVersion = JavaVersion.toVersion(targetJavaVersion)
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
	if (JavaVersion.current() < javaVersion) {
		toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
	}
}
