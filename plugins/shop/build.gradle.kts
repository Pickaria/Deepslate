repositories {
	maven("https://jitpack.io")
	maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
	compileOnly(project(":shared"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
}
