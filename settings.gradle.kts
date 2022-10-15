rootProject.name = "plugin-collection"
include("economy", "shared", "menu", "job", "chat", "shop", "potion")
project(":economy").projectDir = File("plugins/economy")
project(":shared").projectDir = File("plugins/shared")
project(":menu").projectDir = File("plugins/menu")
project(":job").projectDir = File("plugins/job")
project(":chat").projectDir = File("plugins/chat")
project(":potion").projectDir = File("plugins/potion")
project(":shop").projectDir = File("plugins/shop")
