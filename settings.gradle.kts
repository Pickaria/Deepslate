rootProject.name = "plugin-collection"
include("economy", "shared", "menu")
project(":economy").projectDir = File("plugins/economy")
project(":shared").projectDir = File("plugins/shared")
project(":menu").projectDir = File("plugins/menu")
