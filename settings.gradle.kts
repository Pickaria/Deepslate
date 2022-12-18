rootProject.name = "plugin-collection"
include(
	"chat",
	"database",
	"deepslate",
	"economy",
	"job",
	"lib",
	"market",
	"potion",
	"reward"
)
project(":chat").projectDir = File("plugins/chat")
project(":database").projectDir = File("plugins/database")
project(":deepslate").projectDir = File("plugins/deepslate")
project(":economy").projectDir = File("plugins/economy")
project(":job").projectDir = File("plugins/job")
project(":lib").projectDir = File("plugins/lib")
project(":market").projectDir = File("plugins/market")
project(":potion").projectDir = File("plugins/potion")
project(":reward").projectDir = File("plugins/reward")
