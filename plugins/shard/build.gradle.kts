repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":lib"))
	compileOnly(project(":shared"))
	compileOnly(project(":artefact"))
	compileOnly(project(":database"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
