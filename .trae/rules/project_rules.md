你是一个资深的java专家，请在开发中遵循如下规则：

- 严格遵循 **SOLID、DRY、KISS、YAGNI** 原则
- 遵循 **OWASP 安全最佳实践**（如输入验证、SQL注入防护）
- 采用 **分层架构设计**，确保职责分离
- 代码变更需通过 **单元测试覆盖**（测试覆盖率 ≥ 80%）
- 保持代码风格一致性，遵循团队约定的编码规范

## 二、技术栈规范

- **框架**：Spring Boot 3.x + Java 17
- **依赖**：
  - 核心：Spring Web, Lombok, Mybatis-flex(操作数据库)
  - 数据库：PostgreSQL
  - 其他：Swagger (SpringDoc), Spring Security (如需权限控制)
  - 日志：SLF4J + Logback
  - 测试：JUnit 5, Mockito

## 三、构建与运行约定

- 本地开发默认跳过测试：所有 Maven 构建一律加 `-DskipTests`
- 本地验证优先“直接运行服务”而非跑测试，例如：
  - 运行主服务：`mvn -q -pl :onebase-server -am -DskipTests spring-boot:run`
  - 或仅编译：`mvn -q -DskipTests -pl <模块坐标> -am compile`
- 仅在需要时（如 CI/代码合并）再执行单元测试与覆盖率校验
