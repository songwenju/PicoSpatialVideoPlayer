# Skill: 标准化代码提交 (Standardized Commit & Push)

## 触发条件
- 用户要求提交代码时（或执行 `git push` 相关任务时）。

## 执行模版
```text
{Feature|Bugfix}: {Short Description in English}

rootcause:
  1. {原因 A}
  2. {原因 B}
（注意：每条 rootcause 前缩进两个空格）

Ticket: {Ticket_ID}
```

## 执行流程
1. **获取参数**：
   - 询问用户 `Ticket ID`。
   - 询问用户目标分支 `Target Branch`（默认为 `master`）。
2. **分析变更**：
   - 自动分析修改类型（Feature 或 Bugfix）。
   - 总结英文描述和中文 rootcause。
   - **重要：生成的所有描述性字符串（Summary, rootcause）均不要加引号。**
   - **重要：rootcause 下的具体条目（1. 2. ...）必须空两个字符缩进。**
3. **大小检查与豁免**：
   - **自动检查**：统计本次 commit 的总行数及单个文件的最大变更行数。
   - **同一 Ticket 规则**：对于同一个任务/Ticket，必须使用 `git commit --amend` 进行提交，禁止产生多个碎片化的 commit。
   - **打破死循环规则**：由于 `amend` 会改变当前 SHA，豁免 ID 应使用触发规模限制的**初始提交或上一次提交 (Previous SHA)** 的哈希值。在后续 `amend` 中保持该 ID 不变。
   - **触发豁免**：若总行数 > 7500 或单文件变更 > 3000，必须在 `.bytedev/codelines.yaml` 的 `LargeCommits` 列表中添加该豁免 SHA，并在提交信息末尾（Change-Id 之后）同步添加 `ExceptionRules.LargeCommits: {previous_sha}`。
4. **执行提交**：
   - `git add .`
   - `git commit --amend --no-edit` (若仅更新内容) 或更新提交信息。
5. **推送代码**：
   - `git push origin HEAD:refs/for/{Target_Branch}`

## 变更量限制 (Change Size Limits)
- **度量标准**：如果单次 commit 超过 7500 行代码【字节跳动 代码度量 标准统计口径 V1.0（持续扩展）】，或者单个文件超过 3000 行变更，会被默认计为无效。请尽量规避此类情况。
- **豁免规则**：如果确认变更有效且必须，需添加豁免，规则是同时更新 `.bytedev/codelines.yaml` 和提交信息中的 `ExceptionRules.LargeCommits` 为本次提交的 ID。
- **参考资料**：[[用户文档] 代码行统计规则配置]
