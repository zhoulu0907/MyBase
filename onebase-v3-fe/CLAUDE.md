# Claude Code 项目规则

本项目使用 Claude Code 时请遵循以下规则：

## 代码修改规则

### 必须使用 git worktree

使用 Claude Code 进行代码修改时，**务必使用 git worktree** 创建隔离的工作环境：

```
# Claude Code 会自动处理，或手动创建
git worktree add .claude/worktrees/<name> -b <branch-name>
```

这样可以：
- 避免直接在主分支上修改
- 保持工作环境隔离
- 方便回滚和清理

### 必须使用 plan 模式

进行任何非 trivial 的代码修改前，**务必先进入 plan 模式**：

1. Claude Code 会自动进入 plan 模式进行规划
2. 规划完成后需要用户确认才能执行
3. 确保修改符合预期，避免不必要的改动

**trivial 任务例外**：以下简单任务可以不使用 plan 模式：
- 单行修复（typo、简单 bug）
- 文档更新
- 配置文件微调

## 项目信息

- 技术栈：React 18 + TypeScript + Vite + pnpm monorepo
- Node.js 版本：>= v22.17.0
- 包管理器：pnpm >= 9.15.9

## 主要应用

| 应用 | 目录 | 端口 |
|------|------|------|
| App Builder | apps/app-builder | 5173 |
| Admin Console | apps/admin-console | 4400 |
| Runtime | apps/runtime | 9527 |

## 代码规范

- 使用 Arco Design 组件库
- 使用 Formily 表单库
- 类型定义放在 `types.ts`
- 数据转换函数放在 `transform.ts`