# Middleware 配置文件说明

本目录存放后端 API 代理配置文件，由前端网关通过 include 引用。

## 文件说明

| 文件 | 用途 | 必需 |
|------|------|------|
| onebase-upstream.conf | 后端服务 upstream 定义 | 是 |
| onebase-location.conf | 后端 API location 配置 | 是 |

## 使用步骤

### 1. 创建配置文件

```bash
# 复制模板文件（去掉 .example 后缀）
cp onebase-upstream.conf.example onebase-upstream.conf
cp onebase-location.conf.example onebase-location.conf
```

### 2. 修改 upstream 地址

编辑 `onebase-upstream.conf`，修改 server 地址：

```nginx
upstream onebase-backend {
    # 修改为实际后端地址
    server 10.0.1.100:8080;  # 后端服务 IP:端口
}

upstream onebase-flink {
    # 修改为实际 Flink 地址
    server 10.0.1.101:8081;  # Flink 服务 IP:端口
}
```

### 3. 启动前端服务

前端网关会自动挂载此目录并引用配置：

```bash
./deploy.sh up --dist
```

## API 路径

| 路径 | 用途 | 后端服务 |
|------|------|----------|
| /build-api/ | 编辑态 API | onebase-backend |
| /runtime-api/ | 运行态 API | onebase-backend |
| /flink-api/ | Flink API | onebase-flink |

## 配置验证

```bash
# 检查配置文件是否正确加载
podman exec onebase-gateway nginx -t

# 查看实际配置
podman exec onebase-gateway cat /etc/nginx/middleware/onebase-upstream.conf

# 测试 API 代理
curl http://SERVER/build-api/health
```