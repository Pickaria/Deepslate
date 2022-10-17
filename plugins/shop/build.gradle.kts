repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":shared"))
	compileOnly(project(":artefact"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
