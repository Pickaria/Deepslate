repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":shared"))
	compileOnly(project(":economy"))
	compileOnly(project(":menu"))
	compileOnly(project(":potion"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}