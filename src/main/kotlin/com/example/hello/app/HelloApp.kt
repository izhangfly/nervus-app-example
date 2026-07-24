package com.example.hello.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hello.Greeter
import com.nervus.sdk.component.ComponentConfig
import com.nervus.sdk.component.InterfaceRequirement
import com.nervus.sdk.component.NervusApp
import com.nervus.sdk.ui.attachComposeDesktop
import kotlinx.coroutines.launch

class HelloApp : NervusApp(
    config = ComponentConfig(componentId = "hello_app")
) {
    override val requiredInterfaces = listOf(
        InterfaceRequirement(id = "com.example.hello.Greeter"),
    )

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
