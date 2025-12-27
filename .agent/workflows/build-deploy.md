---
description: 构建和部署流程
---

# 构建和部署指南

## 构建流程

### 1. 环境准备
- **JDK**: Java 17
- **Maven**: 3.8+
- **网络**: 能够访问 Maven Central 和公司私服

### 2. 标准构建命令
```bash
# 在项目根目录执行

# 清理并安装所有模块 (跳过测试)
# 适用于本地快速构建
// turbo
mvn clean install -DskipTests -T 8

# 完整构建 (包含测试)
# 适用于 CI 环境
// turbo
mvn clean install -T 8

# 仅编译代码
// turbo
mvn compile -T 8
```

### 3. 模块化构建
如果只修改了某个模块，可以针对性构建以节省时间：

```bash
# 构建指定模块及其依赖
# -pl (projects): 指定模块
# -am (also-make): 同时构建依赖的模块
mvn clean install -pl onebase-module-bpm -am -T 8
```

## 环境配置管理

### 配置文件
采用 Spring Boot 多环境配置：
- `application.yml`: 公共配置
- `application-local.yml`: 本地环境 (默认)
- `application-sit.yml`: sit测试环境
