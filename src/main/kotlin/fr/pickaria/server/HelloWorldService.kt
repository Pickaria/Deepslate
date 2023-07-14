package fr.pickaria.server

import fr.pickaria.controller.economy.balance
import fr.pickaria.helloworld.GreeterGrpcKt
import fr.pickaria.helloworld.HelloRequest
import fr.pickaria.helloworld.helloReply
import fr.pickaria.list_players.*
import fr.pickaria.model.economy.economyConfig
import io.grpc.Server
import io.grpc.ServerBuilder
import org.bukkit.Bukkit
import org.bukkit.Statistic

class GrpcServer(private val port: Int) {
	val server: Server = ServerBuilder
		.forPort(port)
		.addService(HelloWorldService())
		.addService(ListPlayerService())
		.build()

	fun start() {
		server.start()
		println("Server started, listening on $port")
		Runtime.getRuntime().addShutdownHook(
			Thread {
				println("*** shutting down gRPC server since JVM is shutting down")
				this@GrpcServer.stop()
				println("*** server shut down")
			}
		)
	}

	private fun stop() {
		server.shutdown()
	}

	fun blockUntilShutdown() {
		server.awaitTermination()
	}

	internal class HelloWorldService : GreeterGrpcKt.GreeterCoroutineImplBase() {
		override suspend fun sayHello(request: HelloRequest) = helloReply {
			message = "Hello ${request.name}"
		}
	}

	internal class ListPlayerService : ListPlayerServiceGrpcKt.ListPlayerServiceCoroutineImplBase() {
		override suspend fun listPlayer(request: ListPlayerRequest): ListPlayerResponse = listPlayerResponse {
			Bukkit.getOnlinePlayers().map {
				players.add(player {
					name = it.name
				})
			}
		}

		override suspend fun whisper(request: WhisperRequest): WhisperResponse {
			Bukkit.getPlayer(request.player)?.let {
				if (!it.isOnline) {
					return whisperResponse {
						ok = false
					}
				}

				it.sendMessage(request.message)

				return whisperResponse {
					ok = true
				}
			} ?: return whisperResponse {
				ok = false
			}
		}

		override suspend fun getTopTime(request: GetTopTimeRequest) = getTopTimeResponse {
			var sum = 0
			Bukkit.getOfflinePlayers().map {
				times.add(topTime {
					player = it.name ?: "N/A"
					time = it.getStatistic(Statistic.PLAY_ONE_MINUTE)
					sum += time
				})
			}
			totalTime = sum
		}

		override suspend fun getMoney(request: GetMoneyRequest) =
			Bukkit.getPlayer(request.player)?.let {
				getMoneyResponse {
					amount = (economyConfig.currencies[request.currency]?.let { currency ->
						it.balance(currency)
					} ?: 0.0)
				}
			} ?: getMoneyResponse {
				amount = 0.0
			}
	}
}
