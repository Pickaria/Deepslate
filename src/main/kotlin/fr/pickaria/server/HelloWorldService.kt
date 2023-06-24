package fr.pickaria.server

import fr.pickaria.helloworld.GreeterGrpcKt
import fr.pickaria.helloworld.HelloRequest
import fr.pickaria.helloworld.helloReply
import io.grpc.Server
import io.grpc.ServerBuilder

class HelloWorldServer(private val port: Int) {
	val server: Server = ServerBuilder
		.forPort(port)
		.addService(HelloWorldService())
		.build()

	fun start() {
		server.start()
		println("Server started, listening on $port")
		Runtime.getRuntime().addShutdownHook(
			Thread {
				println("*** shutting down gRPC server since JVM is shutting down")
				this@HelloWorldServer.stop()
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
}
