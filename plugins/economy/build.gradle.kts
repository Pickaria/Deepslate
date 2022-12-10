repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":database"))
	compileOnly(project(":lib"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}