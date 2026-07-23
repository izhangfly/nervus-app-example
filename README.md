# Nervus App Example

一个基于 Compose Desktop 的 Nervus OS Hello World 示例。

## 项目结构

```
nervus-app-example/
├── build.gradle.kts              # 构建配置 + nspkg 打包 DSL
├── settings.gradle.kts           # includeBuild nervus-app-sdk / nervus-packaging
├── signing/                      # 签名密钥（自行生成）
│   └── private.pem
└── src/main/kotlin/com/example/hello/
    ├── Greeter.kt                # @Interface 定义
    ├── app/
    │   └── HelloApp.kt           # NervusApp + Compose UI
    └── service/
        └── HelloService.kt       # NervusService 实现
```

## 前置准备

```bash
# 1. 克隆 SDK 和 packaging（与本项目同级）
git clone https://github.com/Nervus-OS/nervus-app-sdk.git
git clone https://github.com/Nervus-OS/nervus-packaging.git

# 2. 生成 Ed25519 签名密钥
mkdir -p signing
openssl genpkey -algorithm ed25519 -out signing/private.pem

# 3. 编译打包
./gradlew nspkg

# 产物在 build/outputs/nspkg/com.example.hello-1.0.0.nspkg
```

## 依赖

- JDK 21+
- nervus-app-sdk（组件基类、IPC、UI）
- nervus-packaging（signing-lib + Gradle 打包插件）

## 说明

App 通过 `@Interface` 定义的 `Greeter` 接口远程调用 Service，结果在 Compose Desktop 窗口中显示。
