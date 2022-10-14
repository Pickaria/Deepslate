repositories {
	maven("https://repo.dmulloy2.net/repository/public/")
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":shared"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
}
