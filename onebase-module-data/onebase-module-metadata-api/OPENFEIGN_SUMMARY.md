## OpenFeign API 实现总结

### 已创建文件清单

#### onebase-module-metadata-api 模块

1. **API 常量**
   - `com.cmsr.onebase.module.metadata.enums.ApiConstants` - 定义服务名和接口前缀

2. **DTO 数据传输对象**
   - `MetadataBackupReqDTO` - 备份请求参数
   - `MetadataBackupRespDTO` - 备份响应数据
   - `MetadataRestoreReqDTO` - 恢复请求参数
   - `MetadataDatasourceDTO` - 数据源传输对象
   - `MetadataBusinessEntityDTO` - 业务实体传输对象
   - `MetadataEntityFieldDTO` - 实体字段传输对象
   - `MetadataEntityRelationshipDTO` - 实体关系传输对象
   - `MetadataValidationRuleDTO` - 校验规则传输对象

3. **OpenFeign 客户端接口**
   - `MetadataBackupApi` - 定义远程调用接口

4. **Maven 依赖配置**
   - 添加了 `spring-cloud-starter-openfeign` 依赖

#### onebase-module-metadata-server 模块

1. **转换器**
   - `MetadataBackupConvert` - DTO/VO/DO 之间的转换器

2. **API 实现**
   - `MetadataBackupApiImpl` - OpenFeign 接口的服务端实现

3. **文档**
   - `README.md` - 完整的使用文档和示例

### 技术架构

```
客户端模块
    ↓ (OpenFeign)
onebase-module-metadata-api
    ↓ (HTTP RPC)
onebase-module-metadata-server
    ↓ (API实现)
MetadataBackupService
    ↓ (业务逻辑)
数据库操作
```

### 接口设计

#### 1. 备份接口
- **路径**: `/rpc-api/metadata/backup/backup`
- **方法**: POST
- **请求**: `MetadataBackupReqDTO` (包含 appId)
- **响应**: `CommonResult<MetadataBackupRespDTO>`

#### 2. 恢复接口
- **路径**: `/rpc-api/metadata/backup/restore`
- **方法**: POST
- **请求**: `MetadataRestoreReqDTO` (包含目标应用ID和所有元数据)
- **响应**: `CommonResult<Boolean>`

### 数据流转

1. **客户端** → 调用 `MetadataBackupApi`
2. **API层** → `MetadataBackupApiImpl` 接收请求
3. **转换层** → `MetadataBackupConvert` 进行 DTO/VO 转换
4. **服务层** → `MetadataBackupService` 执行业务逻辑
5. **数据层** → Repository 操作数据库
6. **响应** → 原路返回转换后的结果

### 使用方式

#### 客户端配置

1. **添加依赖**
```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-module-metadata-api</artifactId>
</dependency>
```

2. **启用Feign**
```java
@EnableFeignClients(basePackages = "com.cmsr.onebase.module.metadata.api")
```

3. **注入使用**
```java
@Resource
private MetadataBackupApi metadataBackupApi;
```

#### 调用示例

```java
// 备份
MetadataBackupReqDTO req = new MetadataBackupReqDTO();
req.setAppId(1L);
CommonResult<MetadataBackupRespDTO> result = metadataBackupApi.backupMetadata(req);

// 恢复
MetadataRestoreReqDTO restoreReq = new MetadataRestoreReqDTO();
restoreReq.setTargetAppId(2L);
// ... 设置各种数据列表
CommonResult<Boolean> restoreResult = metadataBackupApi.restoreMetadata(restoreReq);
```

### 关键特性

1. **类型安全**: 基于强类型的 DTO 定义
2. **服务发现**: 通过服务名自动发现目标服务
3. **负载均衡**: OpenFeign 集成 Ribbon 负载均衡
4. **异常处理**: 统一的异常处理和错误码
5. **文档完整**: Swagger 注解提供 API 文档
6. **转换隔离**: 清晰的 DTO/VO/DO 分层转换

### 扩展建议

1. **熔断器**: 集成 Hystrix 或 Sentinel 进行熔断保护
2. **重试机制**: 配置 Feign 重试策略
3. **监控**: 添加调用链监控和指标收集
4. **缓存**: 对频繁调用的接口添加缓存
5. **版本管理**: API 版本化支持

### 项目集成

- ✅ 遵循项目现有架构模式
- ✅ 保持与 app-api 模块一致的设计风格
- ✅ 完整的 Maven 依赖配置
- ✅ 标准的 Spring Cloud OpenFeign 实现
- ✅ 详细的文档和使用示例

此实现为其他模块提供了简洁、类型安全的远程调用方式，完全符合微服务架构的最佳实践。
