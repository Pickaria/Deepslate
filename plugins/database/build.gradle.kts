val exposedVersion: String by project

dependencies {
	compileOnly(project(":lib"))

	compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
	compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
	compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
	compileOnly("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
	compileOnly("com.h2database:h2:2.1.214")

}
