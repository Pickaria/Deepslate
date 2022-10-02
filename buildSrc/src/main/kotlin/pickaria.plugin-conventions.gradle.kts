import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.distsDirectory
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("pickaria.common-conventions")
	java
}

repositories {
	maven("https://oss.sonatype.org/content/groups/public/")
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
}