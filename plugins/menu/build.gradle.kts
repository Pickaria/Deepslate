repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":shared"))
	compileOnly(project(":shard"))
	compileOnly(project(":lib"))
	compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	compileOnly(kotlin("reflect"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}