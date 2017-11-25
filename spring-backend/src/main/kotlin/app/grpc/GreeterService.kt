package app.grpc

import app.greeter.grpc.GreeterGrpc
import app.greeter.grpc.GreeterOuterClass
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService


@GRpcService
class GreeterService : GreeterGrpc.GreeterImplBase() {
    fun sayHello(request: GreeterOuterClass.HelloRequest, responseObserver: StreamObserver<GreeterOuterClass.HelloResponse>) {
        val replyBuilder = GreeterOuterClass.HelloResponse.newBuilder().setGreeting("Hello " + request.name)
        responseObserver.onNext(replyBuilder.build())
        responseObserver.onCompleted()
    }
}
