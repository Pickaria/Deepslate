repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":lib"))
	compileOnly(project(":economy"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}