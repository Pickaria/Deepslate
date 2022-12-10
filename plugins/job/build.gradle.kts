repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":chat"))
	compileOnly(project(":database"))
	compileOnly(project(":economy"))
	compileOnly(project(":lib"))
	compileOnly(project(":deepslate"))
	compileOnly(project(":potion"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}