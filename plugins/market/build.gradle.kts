repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":shared"))
	compileOnly(project(":menu"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
