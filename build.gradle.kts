plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    id("com.nervus.packaging")
}

group = "com.example"
version = "1.0.0"

dependencies {
    implementation("com.nervus.sdk:nervus-app-sdk:0.1.0")
    implementation(libs.kotlinx.coroutines.core)
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "com.example.hello.app.HelloAppKt"
    }
}

nspkg {
    packageId = "com.example.hello"
    label = "Hello World Example"
    version = project.version.toString()
    versionCode = 1L
    minNervusApi = 1
    targetNervusApi = 1
    supportedAbis = listOf("jvm")

    runtimeDeps.minJavaRelease = 21

    components.app("hello-app") {
        entry = "com.example.hello.app.HelloAppKt"
        runtime = "jvm"
        launchMode = "always-on"
        interfaces = listOf("com.example.hello.Greeter")
    }

    components.service("hello-service") {
        entry = "com.example.hello.service.HelloServiceKt"
        runtime = "jvm"
        launchMode = "on-demand"
    }

    signing.keyFile = file("signing/private.pem")
    signing.role = "developer"
}
