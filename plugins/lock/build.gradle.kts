import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	id("com.github.johnrengelman.shadow")
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

dependencies {
	compileOnly(libs.kotlin.stdlib)

	compileOnly(libs.paper)
	compileOnly(libs.vault)
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "17"
}

tasks.shadowJar {
	destinationDirectory.set(file("$rootDir/server/plugins")) // Output to test server's plugins folder
}