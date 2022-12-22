repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":database"))
	compileOnly(project(":economy"))
	compileOnly(project(":lib"))
	compileOnly(project(":deepslate"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	implementation("net.wesjd:anvilgui:1.6.3-SNAPSHOT")
}
