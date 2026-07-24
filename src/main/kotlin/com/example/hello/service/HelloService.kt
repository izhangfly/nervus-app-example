package com.example.hello.service

import com.example.hello.Greeter
import com.nervus.sdk.component.ComponentConfig
import com.nervus.sdk.component.NervusService
import com.nervus.sdk.component.ProvidedInterface

class HelloService : NervusService(
    config = ComponentConfig(componentId = "hello_service")
) {
    private val greeterImpl = object : Greeter {
        override suspend fun greet(name: String): String {
            return "Hello, $name!"
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

    fun main() {
        start()
        Runtime.getRuntime().addShutdownHook(Thread { close() })
    }
}
