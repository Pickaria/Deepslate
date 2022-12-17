repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":database"))
	compileOnly(project(":deepslate"))
	compileOnly(project(":economy"))
	compileOnly(project(":lib"))
	compileOnly(project(":shard"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
