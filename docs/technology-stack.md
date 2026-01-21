# OneBase v3 Backend - Technology Stack

**Last Updated:** 2026-01-10
**Project:** onebase-v3-be
**Type:** Backend API / Monorepo

---

## Executive Summary

OneBase v3 是一个企业级低代码平台后端系统，采用 Spring Boot 3.4.5 构建，包含 **72+ Maven 模块**的大型 Monorepo 架构。系统集成了工作流引擎 (Flowable 7.0.1)、规则引擎 (LiteFlow 2.15.2)、流程编排引擎、公式计算引擎，并通过 MyBatis-Flex 和 Anyline 双 ORM 实现对 6+ 种数据库的支持。

**关键特性**:
- **模块化单体架构**: 72+ Maven 模块，清晰职责划分，可演进到微服务
- **多租户架构**: 框架级租户隔离、行级数据权限
- **Build/Runtime 分离**: 构建态 (Platform) 和运行态 (Runtime) 独立部署
- **组件化流程**: 40+ 内置节点，灵活的流程编排能力
- **插件化扩展**: 基于 PF4J 的动态插件系统

---

## Core Technologies

### Language & Runtime
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Primary language |
| GraalVM | 23.0.9 | Script execution engine |

### Application Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.4.5 | Core application framework |
| Spring Cloud | 2024.0.1 | Microservice support |
| Spring Cloud Alibaba | 2023.0.3.2 | Alibaba cloud integration |

### Data Access Layer
| Technology | Version | Purpose |
|------------|---------|---------|
| MyBatis-Flex | 1.11.4 | Primary ORM framework |
| MyBatis | 3.5.19 | SQL mapper |
| Anyline | 8.7.2-jdk17 | Multi-database ORM |

**Supported Databases:**
- PostgreSQL 42.7.4
- Oracle 23.26.0.0.0
- DM8 (达梦) 8.1.3.140
- KingBase (人大金仓) 8.6.0
- OpenGauss 5.1.0
- TDengine 3.3.3

### Caching
| Technology | Version | Purpose |
|------------|---------|---------|
| Redisson | 3.52.0 | Redis client with distributed locks |
| JetCache | 2.7.8 | Cache abstraction layer |
| Kryo | 5.6.2 | Serialization |

### Business Process & Rules
| Technology | Version | Purpose |
|------------|---------|---------|
| Flowable | 7.0.1 | BPMN workflow engine (for BPM module) |
| Warm Flow | org.dromara.warm.flow | Lightweight workflow core engine |
| LiteFlow | 2.15.2 | Rule engine and flow orchestration |
| MVEL | 2.4.14.Final | Expression language |
| Apache Commons JEXL | 3.5.0 | Expression evaluator |
| GraalVM JS | 23.0.9 | JavaScript execution in formula engine |

### Messaging & Events
| Technology | Version | Purpose |
|------------|---------|---------|
| RocketMQ Client | 5.0.8 | Message queue |
| Netty | 4.1.116.Final | Network communication |

### API & Documentation
| Technology | Version | Purpose |
|------------|---------|---------|
| SpringDoc OpenAPI | 2.8.3 | API documentation (Swagger) |

### Development Tools
| Technology | Version | Purpose |
|------------|---------|---------|
| Lombok | 1.18.36 | Code generation |
| MapStruct | 1.6.3 | Object mapping |
| Hutool | 5.8.35 | Java utility library |
| Guava | 33.4.8 | Google core libraries |
| EasyExcel | 4.0.3 | Excel processing |

### HTTP Clients
| Technology | Version | Purpose |
|------------|---------|---------|
| Retrofit | 3.0.0 | Type-safe HTTP client |
| OkHttp | 4.12.0 | HTTP client |
| Unirest | 4.5.0 | HTTP client |

### Security & Auth
| Technology | Version | Purpose |
|------------|---------|---------|
| Lock4j | 2.2.7 | Distributed locking |
| JustAuth | 1.16.7 | Third-party login |
| Weixin-Java | 4.7.5.B | WeChat integration |

### Testing
| Technology | Version | Purpose |
|------------|---------|---------|
| Maven Surefire | 3.2.2 | Test runner (JUnit 5) |
| Mockito Inline | 5.2.0 | Mocking framework |
| Podam | 8.0.2 | Test data generator |
| Jedis Mock | 1.1.4 | Redis mock |

### Plugin Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| PF4J | 3.14.0 | Plugin framework core |
| PF4J Spring | 0.10.0 | Spring integration for PF4J |
| Maven Plugin API | 3.13.1 | Custom Maven plugin for plugin packaging |

### Other Libraries
| Technology | Version | Purpose |
|------------|---------|---------|
| Jackson | 2.18.3 | JSON processing |
| Gson | 2.13.2 | JSON library |
| Commons IO | 2.17.0 | I/O utilities |
| Commons Collections | 4.5.0 | Collection utilities |
| Dom4j | 2.2.0 | XML processing |
| Apache Tika | 3.1.0 | Content detection |
| TTL (TransmittableThreadLocal) | 2.15.1 | Thread context propagation |
| UID Generator | - | Distributed unique ID generation |

### AI Integration (Optional)
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring AI Alibaba | 1.0.0-M5 | Alibaba AI integration |
| Tongyi Qianwen | - | LLM for dashboard generation |

---

## Architecture Pattern

**Type:** Modular Layered Architecture with Domain-Driven Design

**Key Characteristics:**
- **Monorepo Structure:** 72 Maven modules organized into functional domains
- **Layered Design:**
  - Framework layer (`onebase-framework`)
  - Server layer (`onebase-server`, `onebase-server-platform`, `onebase-server-runtime`)
  - Module layer (system, infra, app, data, flow, formula, bpm)
  - Each module follows: API → Core → Runtime → Build structure
- **Multi-tenancy Support:** Built-in tenant isolation
- **Pluggable Modules:** Each business domain is a separate module

**Module Organization:**
```
onebase-cloud (root)
├── onebase-dependencies (BOM)
├── onebase-framework (core framework)
├── onebase-server* (main application servers)
└── onebase-module-* (business modules)
    ├── onebase-module-system (system management)
    ├── onebase-module-infra (infrastructure)
    ├── onebase-module-app (application)
    ├── onebase-module-data (data platform)
    ├── onebase-module-flow (workflow)
    ├── onebase-module-formula (formula engine)
    └── onebase-module-bpm (business process)
```

---

## Build Configuration

- **Build Tool:** Maven 3.x
- **Java Version:** 17
- **Encoding:** UTF-8
- **Key Plugins:**
  - maven-compiler-plugin 3.14.0 (with -parameters flag)
  - maven-surefire-plugin 3.2.2 (JUnit 5)
  - flatten-maven-plugin 1.6.0 (version management)
- **Annotation Processing:** Lombok + MapStruct + Spring Boot Configuration Processor

---

## Maven Repositories

- **Primary:** Huawei Cloud Maven Mirror
- **Secondary:** Aliyun Maven Mirror
- **Snapshots:** Maven Central Snapshots (for Anyline)

