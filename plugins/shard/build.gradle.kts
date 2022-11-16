repositories {
	maven("https://jitpack.io")
	maven("https://maven.quozul.dev/snapshots")
}

dependencies {
	compileOnly(project(":artefact"))
	compileOnly(project(":database"))
	compileOnly(project(":economy"))
	compileOnly(project(":lib"))
	compileOnly(project(":shared"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	compileOnly("fr.pickaria:warden:1.0-SNAPSHOT")
}
