rootProject.name = "plugin-collection"
include("economy", "shared", "menu", "job")
project(":economy").projectDir = File("plugins/economy")
project(":shared").projectDir = File("plugins/shared")
project(":menu").projectDir = File("plugins/menu")
project(":job").projectDir = File("plugins/job")