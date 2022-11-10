repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":chat"))
	compileOnly(project(":database"))
	compileOnly(project(":economy"))
	compileOnly(project(":lib"))
	compileOnly(project(":newmenu"))
	compileOnly(project(":potion"))
	compileOnly(project(":shared"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}