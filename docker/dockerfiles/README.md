# 打包说明

打包分为`基础JVM打包`与`成品打包`两种

## 基础JVM打包

该打包方式仅会打包运行需要的JVM，即`onebase-jvm:v1.0.0`。
适用于JAR包需要频繁替换的场景。

## 成品打包

该打包方式会将编辑态、运行态、Flink服务分开打包为整体镜像。适用于产品稳定后交付给运维部署的场景。

# 打包准备

在项目目录(`ROOT<onebase-v3-be>/`)下执行打包命令。
```bash
mvn clean package -DskipTests
```

# 打包示例

## 成品打包
```bash
sh build-image.sh all
```

## 基础JVM打包
```bash
sh build-image.sh jvm
```

# 镜像加载

将镜像打包后的`.tar`文件上传至服务器后，使用以下命令加载镜像。
```bash
# 以oneabse-be-build-1.0.0.tar为例
docker load -i onebase-be-build-1.0.0.tar
```