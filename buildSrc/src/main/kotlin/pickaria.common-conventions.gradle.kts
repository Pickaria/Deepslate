import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.distsDirectory
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm")
	java
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
}

tasks.withType<KotlinCompile>{
	kotlinOptions.jvmTarget = "17"
}

java {
	val javaVersion = JavaVersion.VERSION_17
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}

tasks.withType<Jar> {
	destinationDirectory.set(file("$rootDir/server/plugins")) // Output to test server's plugins folder
}