import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java")
	kotlin("jvm") version "1.8.20"
	kotlin("plugin.serialization") version "1.8.20"
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	maven("https://oss.sonatype.org/content/groups/public/")
	maven("https://repo.papermc.io/repository/maven-public/") // Paper
	maven("https://maven.quozul.dev/snapshots")
	maven("https://repo.aikar.co/content/groups/aikar/") // ACF
	maven("https://jitpack.io") // Vault
	maven("https://repo.glaremasters.me/repository/towny/") // Towny
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
	compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
	compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
	compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
	compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
	compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.11.0")
	compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.11.0")
	compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
	compileOnly("com.palmergames.bukkit.towny:towny:0.99.0.0")
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	compileOnly("org.jetbrains.exposed:exposed-core:0.41.1")
	compileOnly("org.jetbrains.exposed:exposed-dao:0.41.1")
	compileOnly("org.jetbrains.exposed:exposed-jdbc:0.41.1")
	compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.41.1")
	compileOnly("com.h2database:h2:2.1.214")
	compileOnly("fr.pickaria:bedrock:1.0.18-SNAPSHOT")
	compileOnly("fr.pickaria:spawner:1.0.8-SNAPSHOT")
	compileOnly("com.charleskorn.kaml:kaml:0.53.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "17"
}

tasks {
	compileKotlin {
		kotlinOptions.javaParameters = true
	}

	jar {
		destinationDirectory.set(file("server/plugins"))
		from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
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
