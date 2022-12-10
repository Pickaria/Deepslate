repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":lib"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
