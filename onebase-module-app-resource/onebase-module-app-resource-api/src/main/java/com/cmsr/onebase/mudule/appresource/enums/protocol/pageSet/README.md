# PageSet 页面集合模块

## 概述

PageSet 模块是 OneBase 平台中用于定义和管理页面集合的核心组件。它提供了一套完整的页面组合配置机制，支持列表页面、详情页面和编辑页面的统一管理。

## 模块结构

```
pageSet/
├── PageSetSpec.java      # 页面集合规格定义
├── PageMetadata.java     # 页面级核心数据
├── PageRefs.java         # 页面引用配置
├── PageRef.java          # 页面引用定义
├── RouterParam.java      # 路由参数定义
└── README.md            # 本文档
```

## 核心类说明

### PageSetSpec
页面集合规格定义的主类，包含页面级核心数据和页面引用配置。

**主要字段：**
- `metadata`: 页面级核心数据，包含主元数据和元数据列表
- `pageRefs`: 页面引用配置，包含列表、详情、编辑页面配置

### PageMetadata
页面级核心数据定义，用于描述页面集合的元数据信息。

**主要字段：**
- `mainMetadata`: 主元数据标识
- `metadatas`: 元数据列表，支持多个元数据

### PageRefs
页面引用配置容器，组织不同类型的页面引用。

**主要字段：**
- `listPages`: 列表页面配置列表
- `detailPages`: 详情页面配置列表
- `editPages`: 编辑页面配置列表

### PageRef
单个页面引用定义，描述页面的基本信息和配置。

**主要字段：**
- `name`: 页面名称
- `ref`: 页面引用标识
- `defaultSeq`: 默认排序序号
- `title`: 页面显示标题
- `default`: 是否为默认页面
- `routerParams`: 路由参数配置列表

### RouterParam
路由参数定义，用于页面路由的参数配置。

**主要字段：**
- `name`: 参数名称
- `type`: 参数类型

## YAML 配置示例

```yaml
apiVersion: page.cm-iov/v1alpha1
kind: PageSet
metadata:
    name: "user-page-set"
    version: "1.0.0"
    type: "page-set"
    displayName: "页面组合"
    description: "整合页面功能的页面集合"
    labels: []
spec:
    # 页面级核心数据
    metadata:
        mainMetadata: "Order"
        metadatas:
            - "Order"
            - "OrderItem"

    pageRefs:
        listPages:
            # 常规表单页面
            - name: "order-list-searchtable"
              ref: "order-list-searchtable"
              defaultSeq: 0
              title: "订单列表"
              default: true
            # 卡片表单页面
            - name: "order-list-card"
              ref: "order-list-card"
              defaultSeq: 10
              title: "订单卡片列表"
        detailPages:
            # 详情页面
            - name: "order-detail-page"
              ref: "order-detail-page"
              title: "常规详情表单"
              routerParams:
                  - name: id
                    type: string
              default: true
            # 指定流程节点详情页面
            - name: "order-detail-bpmnode-page"
              ref: "order-detail-bpmnode-page"
              title: "常规流程详情表单"
              routerParams:
                  - name: id
                    type: string
                  - name: bpmTaskId
                    type: string
                  - name: bpmNodeId
                    type: string
        editPages:
            # 新建或编辑状态表单
            - name: "order-detail-page"
              ref: "order-detail-page"
              title: "常规编辑表单"
              default: true
```

## Java 使用示例

### 创建页面集合配置

```java
// 创建路由参数
RouterParam idParam = new RouterParam("id", "string");
RouterParam taskIdParam = new RouterParam("bpmTaskId", "string");

// 创建页面引用
PageRef listPage = new PageRef();
listPage.setName("order-list-searchtable");
listPage.setRef("order-list-searchtable");
listPage.setDefaultSeq(0);
listPage.setTitle("订单列表");
listPage.setDefault(true);

PageRef detailPage = new PageRef();
detailPage.setName("order-detail-page");
detailPage.setRef("order-detail-page");
detailPage.setTitle("常规详情表单");
detailPage.setRouterParams(Arrays.asList(idParam));
detailPage.setDefault(true);

// 创建页面引用配置
PageRefs pageRefs = new PageRefs();
pageRefs.setListPages(Arrays.asList(listPage));
pageRefs.setDetailPages(Arrays.asList(detailPage));

// 创建页面元数据
PageMetadata metadata = new PageMetadata();
metadata.setMainMetadata("Order");
metadata.setMetadatas(Arrays.asList("Order", "OrderItem"));

// 创建页面集合规格
PageSetSpec spec = new PageSetSpec();
spec.setMetadata(metadata);
spec.setPageRefs(pageRefs);
```

### 序列化和反序列化

```java
import com.fasterxml.jackson.databind.ObjectMapper;

ObjectMapper mapper = new ObjectMapper();

// 序列化为JSON
String json = mapper.writeValueAsString(spec);

// 从JSON反序列化
PageSetSpec specFromJson = mapper.readValue(json, PageSetSpec.class);
```

## 设计原则

### 1. 单一职责原则
每个类都有明确的职责：
- `PageSetSpec`: 整体规格定义
- `PageMetadata`: 元数据管理
- `PageRefs`: 页面引用组织
- `PageRef`: 单个页面配置
- `RouterParam`: 路由参数定义

### 2. 可扩展性
- 支持多种页面类型（列表、详情、编辑）
- 灵活的路由参数配置
- 可扩展的元数据结构

### 3. 类型安全
- 使用强类型定义，避免运行时错误
- 完整的字段注释和文档

### 4. 配置驱动
- 通过YAML配置文件定义页面集合
- 支持环境特定的配置

## 最佳实践

### 1. 命名规范
- 页面名称使用 kebab-case 格式
- 引用标识符使用有意义的名称
- 路由参数名称简洁明了

### 2. 配置管理
- 将配置与代码分离
- 使用版本控制管理配置文件
- 支持多环境配置

### 3. 错误处理
- 验证配置的完整性
- 提供有意义的错误信息
- 支持配置的向后兼容

### 4. 性能考虑
- 避免过深的嵌套结构
- 合理使用默认值
- 缓存常用的配置信息

## 依赖关系

- **Lombok**: 用于简化代码（@Data, @AllArgsConstructor, @NoArgsConstructor）
- **Jackson**: 用于JSON序列化/反序列化
- **Spring Boot**: 集成到Spring Boot应用中

## 版本历史

- **v1.0.0**: 初始版本，支持基本的页面集合配置
- 支持列表、详情、编辑页面类型
- 支持路由参数配置
- 支持元数据管理

## 贡献指南

1. 遵循现有的代码风格和命名规范
2. 添加完整的JavaDoc注释
3. 编写相应的单元测试
4. 更新本文档以反映变更

## 许可证

本项目遵循项目的整体许可证协议。