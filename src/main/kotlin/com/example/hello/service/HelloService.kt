package com.example.hello.service

import com.example.hello.Greeter
import com.nervus.sdk.component.ComponentConfig
import com.nervus.sdk.component.NervusService
import com.nervus.sdk.component.ProvidedInterface

class HelloService : NervusService(
    config = ComponentConfig(
        componentId = "hello_service",
        socketPath = "/run/nervus/nervud.sock",
    )
) {
    private val greeterImpl = object : Greeter {
        override suspend fun greet(name: String): String {
            val reply = "Hello, $name!"
            logger.info("greet called name=[$name] reply=[$reply]")
            return reply
        }
    }

    override val providedInterfaces = listOf(
        ProvidedInterface(
            id = "com.example.hello.Greeter",
            major = 1,
            minor = 0,
            instance = greeterImpl,
        ),
    )

    override fun onReady() {
        logger.info("HelloService ready, greeting interface registered")
    }

    override fun onStop() {
        logger.info("HelloService stopping")
    }

}

fun main() {
    val service = HelloService()
    Runtime.getRuntime().addShutdownHook(Thread { service.close() })
    service.start()
}
