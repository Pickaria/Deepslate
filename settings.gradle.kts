rootProject.name = "plugin-collection"
include("economy", "shared")
project(":economy").projectDir = File("plugins/economy")
project(":shared").projectDir = File("plugins/shared")
