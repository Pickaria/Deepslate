rootProject.name = "plugin-collection"
include("economy", "shared", "menu", "job", "chat", "shard", "potion", "artefact")
project(":economy").projectDir = File("plugins/economy")
project(":shared").projectDir = File("plugins/shared")
project(":menu").projectDir = File("plugins/menu")
project(":job").projectDir = File("plugins/job")
project(":chat").projectDir = File("plugins/chat")
project(":shard").projectDir = File("plugins/shard")
project(":potion").projectDir = File("plugins/potion")
project(":artefact").projectDir = File("plugins/artefact")
