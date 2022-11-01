dependencies {
	compileOnly(project(":lib"))
	compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	compileOnly(kotlin("reflect"))
}