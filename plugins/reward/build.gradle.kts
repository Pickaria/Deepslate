repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":economy"))
	compileOnly(project(":lib"))
	compileOnly(project(":menu"))
	compileOnly(project(":shard"))
	compileOnly(project(":shared"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	compileOnly("fr.pickaria:warden:1.0-SNAPSHOT")
}
