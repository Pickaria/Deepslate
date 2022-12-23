repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":deepslate"))
	compileOnly(project(":potion"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}