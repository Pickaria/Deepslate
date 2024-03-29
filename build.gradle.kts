plugins {
    id("java")
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}

group = "fr.pickaria"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
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
    compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
    compileOnly("fr.pickaria:spawner:1.0.9-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")

    implementation("com.charleskorn.kaml:kaml:0.53.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.12.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.12.0")
    implementation("com.h2database:h2:2.2.222")
    implementation("fr.pickaria:bedrock:1.0.21")
    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.43.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
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