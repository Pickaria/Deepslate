rootProject.name = "plugin-collection"
include("economy", "shared", "menu", "job", "teleport")
project(":economy").projectDir = File("plugins/economy")
project(":shared").projectDir = File("plugins/shared")
project(":menu").projectDir = File("plugins/menu")
project(":job").projectDir = File("plugins/job")
project(":teleport").projectDir = File("plugins/teleport")
