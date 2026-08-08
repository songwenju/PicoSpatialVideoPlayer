# 项目命名规范 (Naming Skills)

在本项目中新建任何文件、类、函数或资源时，必须严格遵守以下命名规范：

| 类型 | 命名风格 | 示例 / 说明 |
| :--- | :--- | :--- |
| **Package 包名** | 全小写 (Lower Case) | `com.picoxr.videoplayer`, `com.picoxr.mediapack.common` |
| **Resource 资源目录** | 小写 + 下划线 (Snake Case) | `drawable-xxhdpi`, `layout`, `values-zh` |
| **Resource 文件名** | 小写 + 下划线 (Snake Case) | `ic_video_play.xml`, `ic_3d_top.png`, `strings.xml` |
| **Kotlin/Java 类名** | 大驼峰 (Upper Camel Case) | `VideoPlayerController.kt`, `MainActivity.kt` |
| **Compose 组件名** | 大驼峰 (Upper Camel Case) | `MainApp.kt`, `VideoPlaybackView.kt` (函数名也用大驼峰) |
| **Compose 文件名** | 大驼峰 (Upper Camel Case) | `MainApp.kt`, `OverlayEvent.kt` (通常与主 Composable 同名) |
| **Prompt/Config 文件** | 大驼峰 或 小写下划线 | `SystemPrompts.kt` (代码内) 或 `agent_config.json` (资源内) |
| **变量 / 函数名** | 小驼峰 (Lower Case) | `sendMessage()`, `isModelThinking`, `currentPlayItem` |
| **单测文件** | 大驼峰 + `Test` 后缀 | `MediaItemTest.kt`, `ProxyUtilsTest.kt` |

## 核心原则
1. **一致性**：所有新加内容必须与上述列表保持一致。
2. **包名禁止大写**：确保 `android.namespace` 和 `applicationId` 以及对应的目录结构均为全小写。
3. **资源前缀**：图标类资源建议以 `ic_` 开头。
4. **Compose 规范**：Compose 相关文件和组件函数均采用大驼峰命名。
