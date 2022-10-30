repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":lib"))
	compileOnly(project(":database"))
	compileOnly(project(":shared"))
	compileOnly(project(":economy"))
	compileOnly(project(":menu"))
	compileOnly(project(":potion"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}