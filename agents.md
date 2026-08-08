# Testing Generation Guidelines for PicoVideoPlayer

## Testing Standards and Quality Requirements
- **High Success Rate**: All tests must pass without compilation or syntax errors.
- **High Coverage**: Minimum 85% coverage for unit tests in business logic.
- **No Flaky Tests**: Deterministic behavior without random failures.
- **Consistent Style**: Follow project coding standards (Kotlin, ECS architecture).

## Test Generation Rules for AI/Developers

### 1. Understanding the Context
Before writing tests for this project, understand:
- **ECS (Entity-Component-System)**: Core logic in `playbackcore` follows ECS.
- **Spatial UI**: Interaction with Pico Spatial UI components.
- **Media Playback**: Logic around `PXRPlayer` and `CypressPlayer`.

### 2. Test Coverage Requirements
- **Happy Path**: Normal successful execution.
- **Boundary Conditions**: Null safety, empty lists, extreme values (e.g., seek to end).
- **Error Handling**: Player errors, database failures, file not found.
- **State Machine**: Verify transitions between `PlaybackState`.

### 3. Mocking Strategy
- **Framework**: Use **MockK** for all mocking needs.
- **Android Framework**: Use **Robolectric** when Android framework components (Context, Intent, Bundle) are involved.
- **Manual Fakes**: Preferred for internal interfaces if MockK becomes too complex.
- **No Real IO**: Never perform real network or file system operations.

### 4. Test Structure (Kotlin Style)
Use descriptive names and follow the "Given-When-Then" pattern.

```kotlin
@Test
fun `when playing a video then state should transition to PLAYING`() {
    // Given
    val player = mockk<PXRPlayer>(relaxed = true)
    
    // When
    player.play()
    
    // Then
    verify { player.play() }
}
```

For table-driven tests in Kotlin:
```kotlin
@Test
fun `test icon resource by progress`() {
    val testCases = listOf(
        0.0f to R.drawable.ic_volume_mute,
        0.2f to R.drawable.ic_volume_min,
        0.5f to R.drawable.ic_volume_loud,
        0.8f to R.drawable.ic_volume_loudest
    )
    
    testCases.forEach { (input, expected) ->
        assertEquals(expected, UIUtils.getIconResByProgress(input))
    }
}
```

### 5. Deterministic Testing
- Use fixed values for time-dependent logic.
- Avoid thread sleep; use `runTest` for coroutines.
- Clean up resources in `@After` or `runTest` cleanup blocks.

### 6. Skill Execution
- **Mandatory Skills**: Always call and refer to the contents under the `.trae/skills` directory during each execution.

### 7. 沟通与解释规则
- 所有对用户的技术说明、修复报告、设计说明均使用中文进行解释与说明。
- 每次修复完 bug 都必须补充对问题本质原因的解释，至少包括：触发条件、异常或错误链路、修复的核心原理与风险评估。

### 8. 提交与关键词触发
- 当用户指令中包含“提交代码”关键词时，必须使用 `.trae/skills/standard_commit.md` 定义的标准化提交流程与模板进行提交。
- 提交信息需符合模板结构：
  - 首行标题必须使用英文，格式为 `Feature|Bugfix: ...`；
  - 首行中的冒号后必须有一个空格；
  - 首行之后必须有一个空行（用于分隔标题与正文）；
  - `rootcause:` 段必须使用中文撰写，每条两空格缩进编号,不添加其他的说明；
  - 最后一行必须是 `Ticket: <ID>`，且该行行首需预留一个空格（例如：` Ticket: 123456`）。
  - 提交时根据提供的ticket，在meego中判断提交类型（`Bugfix` 或 `Feature`）。
- 同一 Ticket 的连续提交必须使用 `git commit --amend`，避免产生碎片化提交；默认目标分支为 `master`，自动推送到远端:git push origin HEAD:refs/for/master ，如需其它分支须在指令中明确说明。
