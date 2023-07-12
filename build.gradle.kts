plugins {
	id("java")
	kotlin("jvm") version "1.8.20"
	kotlin("plugin.serialization") version "1.8.20"
	id("com.google.protobuf") version "0.9.3"
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

ext["grpcVersion"] = "1.54.1"
ext["grpcKotlinVersion"] = "1.3.0" // CURRENT_GRPC_KOTLIN_VERSION
ext["protobufVersion"] = "3.22.3"
ext["coroutinesVersion"] = "1.7.0"

allprojects {
	repositories {
		mavenCentral()
		google()
	}
}

repositories {
	maven("https://repo.papermc.io/repository/maven-public/") // Paper
	maven("https://repo.aikar.co/content/groups/aikar/") // ACF
	maven("https://jitpack.io") // Vault

	maven {
		url = uri("https://maven.pkg.github.com/Pickaria/Bedrock")

		credentials {
			username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
			password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
		}
	}

	maven {
		url = uri("https://maven.pkg.github.com/Pickaria/Spawner")

		credentials {
			username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
			password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
		}
	}
}

dependencies {
	implementation(project(":protos"))

	compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
	compileOnly("fr.pickaria:spawner:1.0.10-SNAPSHOT")
	compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
	compileOnly("net.luckperms:api:5.4")

	implementation("com.charleskorn.kaml:kaml:0.53.0")
	implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.12.0")
	implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.12.0")
	implementation("com.h2database:h2:2.1.214")
	implementation("fr.pickaria:bedrock:1.0.20-SNAPSHOT")
	implementation("fr.pickaria:bedrock:1.0.21")
	implementation("org.jetbrains.exposed:exposed-core:0.41.1")
	implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
	implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
	implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.41.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
	runtimeOnly("io.grpc:grpc-netty:${rootProject.ext["grpcVersion"]}")
}

tasks {
	compileKotlin {
		kotlinOptions.jvmTarget = "17"
		kotlinOptions.javaParameters = true
	}

	jar {
		duplicatesStrategy = DuplicatesStrategy.INCLUDE

		destinationDirectory.set(file("server/plugins"))

		manifest {
			attributes["Main-Class"] = "fr.pickaria.MainKt"
		}

		from(configurations.runtimeClasspath.get().map { zipTree(it) })
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