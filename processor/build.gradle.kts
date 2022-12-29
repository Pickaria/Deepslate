plugins {
	kotlin("jvm")
}

version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.google.devtools.ksp:symbol-processing-api:1.7.22-1.0.8")
}