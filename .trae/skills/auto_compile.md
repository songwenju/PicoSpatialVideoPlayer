# Skill: 自动编译校验 (Auto-Compile Verification)

## 触发条件
- 每次使用 `Edit` 或 `Write` 工具成功修改代码后。

## 执行步骤
1. **识别修改模块**：判断修改的文件属于哪个 Android 模块（如 `:app`, `:mediapack-ui`, `:localdemo`）。
2. **执行编译命令**：
   - 默认完整校验：`./gradlew :app:assembleChinaDebug :localdemo:assembleDebug`
   - 针对性校验：根据修改的模块执行对应的 `assembleDebug` 任务。
3. **错误处理**：
   - 如果编译失败，读取错误日志并自动尝试修复。
   - 循环执行直到编译通过。
4. **状态反馈**：只有在编译通过后，才告知用户修改已完成。
