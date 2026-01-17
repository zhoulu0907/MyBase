# Git 推送代理配置说明

**日期**: 2026-01-17
**问题**: 项目远程仓库是内网地址，与全局代理配置冲突

## 背景

- 项目远程仓库：`http://git.virtueit.net/s25029301301/onebase-v3-be.git`（内网）
- 全局代理配置：`http://127.0.0.1:7890`（外网代理）
- 冲突：通过代理无法访问内网Git仓库

## 推送流程

每次推送到远程仓库时，都需要执行以下步骤：

### 方法1：手动执行命令

```bash
# 1. 取消代理
git config --global --unset http.proxy
git config --global --unset https.proxy

# 2. 推送代码
git push origin dev

# 3. 恢复代理
git config --global http.proxy http://127.0.0.1:7890
git config --global https.proxy http://127.0.0.1:7890
```

### 方法2：使用脚本（推荐）

创建便捷脚本 `git-push-internal.sh`：

```bash
#!/bin/bash
# onebase-v3-be 项目专用推送脚本

echo "🔄 取消代理配置..."
git config --global --unset http.proxy
git config --global --unset https.proxy

echo "⬆️  推送到远程仓库..."
git push origin dev

PUSH_STATUS=$?

echo "🔄 恢复代理配置..."
git config --global http.proxy http://127.0.0.1:7890
git config --global https.proxy http://127.0.0.1:7890

if [ $PUSH_STATUS -eq 0 ]; then
    echo "✅ 推送成功"
else
    echo "❌ 推送失败"
    exit $PUSH_STATUS
fi
```

使用方法：

```bash
# 在项目根目录执行
chmod +x git-push-internal.sh
./git-push-internal.sh
```

### 方法3：使用 Git Alias

在 `~/.gitconfig` 中添加别名：

```ini
[alias]
    push-internal = "!f() { git config --global --unset http.proxy && git config --global --unset https.proxy && git push origin dev && git config --global http.proxy http://127.0.0.1:7890 && git config --global https.proxy http://127.0.0.1:7890; }; f"
```

使用方法：

```bash
git push-internal
```

## 原理说明

### Git 代理配置优先级

1. **全局代理**（`git config --global http.proxy`）
   - 影响所有Git仓库
   - 本项目配置：`http://127.0.0.1:7890`

2. **取消代理**
   - `git config --global --unset http.proxy`
   - 移除全局代理配置，直接连接

3. **恢复代理**
   - `git config --global http.proxy http://127.0.0.1:7890`
   - 重新设置全局代理，影响其他仓库

### 为什么不能使用局部代理？

Git 局部代理配置（`git config --local http.proxy`）只对特定仓库生效，但：

1. 本项目的远程仓库是内网地址，不需要代理
2. 其他项目（如 GitHub）需要代理
3. 使用 `--unset` 可以临时禁用全局代理

## 替代方案

### 方案A：Git 配置条件代理（未验证）

在 `~/.gitconfig` 中配置：

```ini
[http "http://git.virtueit.net/"]
    proxy = ""

[http]
    proxy = http://127.0.0.1:7890
```

**注意**：此方案未验证，可能不生效。

### 方案B：SSH 替代 HTTPS

如果远程仓库支持 SSH，可以切换协议：

```bash
# 切换为 SSH
git remote set-url origin git@git.virtueit.net:s25029301301/onebase-v3-be.git

# 推送（SSH 不受 HTTP 代理影响）
git push origin dev
```

## 注意事项

1. **推送后务必恢复代理**
   - 否则其他需要代理的项目（如 GitHub）无法访问

2. **网络检查**
   - 确保能直接访问内网Git仓库
   - 检查VPN或内网连接状态

3. **分支确认**
   - 当前开发分支：`dev`
   - 主分支：`master`
   - 根据实际情况修改推送命令

## 相关命令

### 查看当前代理配置

```bash
git config --global --get http.proxy
git config --global --get https.proxy
```

### 查看远程仓库

```bash
git remote -v
```

### 查看推送状态

```bash
git status
git log --oneline -3
```

## 历史记录

| 日期 | 操作 | 说明 |
|------|------|------|
| 2026-01-17 | 首次记录 | 发现内网仓库需要取消代理才能推送 |

## 参考

- [Git Proxy Documentation](https://git-scm.com/docs/git-config)
- 项目路径：`/Users/kanten/IdeaProjects/ob3.0-dev/onebase-v3-be`
