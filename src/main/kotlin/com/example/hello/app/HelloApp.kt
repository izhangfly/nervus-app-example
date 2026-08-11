package com.example.hello.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hello.Greeter
import com.nervus.sdk.component.ComponentConfig
import com.nervus.sdk.component.NervusApp
import com.nervus.sdk.component.ProvidedInterface
import com.nervus.sdk.ui.attachComposeDesktop
import kotlinx.coroutines.launch

class HelloApp : NervusApp(
    config = ComponentConfig(componentId = "hello_app")
) {
    // HelloApp 自己实现 Greeter，不再依赖独立的 hello_service 组件。
    // app 组件 CanProvideInterfaces() == true（nervud pkgregistry），
    // 接口与 UI 捆绑在同一进程里，省去跨组件 IPC 往返。
    //
    // schemaHash 暂留 ByteArray(0)：用户应用的 provider.binpb / schemas.binpb
    // 生成工具链尚未实现（需要 nervus-ipc 出通用 providergen CLI）。
    // 在此之前 RegisterEndpoint 会被内核以 "schema hash does not match" 拒绝，
    // 本 app 不可部署；等工具链就绪后补上 exports + schemaHash。
    private val greeterImpl = object : Greeter {
        override suspend fun greet(name: String): String = "Hello, $name!"
    }

    override val providedInterfaces = listOf(
        ProvidedInterface(
            id = "com.example.hello.Greeter",
            major = 1,
            instance = greeterImpl,
            // TODO(provider-toolchain): populate schemaHash from generated binpb
        ),
    )

    // 本包内直接 use()，Resolve 到自己提供的 greeterImpl，无需 requiredInterfaces。
    private var greeter: Greeter? = null

    override fun onReady() {
        greeter = use(Greeter::class)
        logger.info("HelloApp ready, launching UI...")
        attachComposeDesktop(
            title = "Nervus Hello World",
            width = 400.dp,
            height = 300.dp,
        ) {
            HelloScreen(greeter!!)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelloScreen(greeter: Greeter) {
    var name by remember { mutableStateOf("World") }
    var result by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Nervus Hello World") })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Button(
                    onClick = {
                        loading = true
                        result = ""
                        scope.launch {
                            try {
                                val reply = greeter.greet(name)
                                result = reply
                            } catch (e: Exception) {
                                result = "Error: ${e.message}"
                            } finally {
                                loading = false
                            }
                        }
                    },
                    enabled = name.isNotBlank() && !loading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Greet")
                }

                if (result.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    ) {
                        Text(
                            text = result,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}
