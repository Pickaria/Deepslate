val exposedVersion: String by project

plugins {
	kotlin("plugin.serialization") version "1.7.20"
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

	implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
	implementation("com.h2database:h2:2.1.214")
	implementation("fr.pickaria:bedrock:1.0.2-SNAPSHOT")
}
