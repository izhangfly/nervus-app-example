// HelloService 已废弃。Greeter 接口现在由 HelloApp 直接提供（app 组件捆绑服务）。
// 这个文件仅作历史参考，不再编译进任何组件。
//
// 背景：早期设计把 Greeter 实现放在独立的 on-demand service 组件里，
// app 通过 IPC 调用。改为捆绑后：
//   - 省去跨组件 IPC 往返
//   - 接口与 UI 同生命周期，不需要单独管理 service 的启动
//   - 去掉了一个 schemaHash = ByteArray(0) 的注册（工具链就绪前无法正确填值）
//
// 如需恢复独立 service 形态，参考 nervus-system-server 的 providergen 方案：
// 应用自己维护 .proto，用 nervus-ipc 的通用 providergen CLI 产出 binpb，
// 打包时由 Gradle plugin 的 provider { fromProto(...) } 引入。
