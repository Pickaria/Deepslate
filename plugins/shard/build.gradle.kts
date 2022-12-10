repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":artefact"))
	compileOnly(project(":database"))
	compileOnly(project(":economy"))
	compileOnly(project(":lib"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	compileOnly("fr.pickaria:warden:1.0-SNAPSHOT")
}
