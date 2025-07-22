# OneBase管理后台脚手架
## 核心指令
单体模式打包命令：
Mac:  mvn package -am -pl onebase-server -Dmaven.test.skip=true
Win:  mvn package -am -pl onebase-server '-Dmaven.test.skip=true'

Cloud模式打包命令：
Mac: mvn clean package -Dmaven.test.skip=true
Win: mvn clean package '-Dmaven.test.skip=true'

服务启动：
sh deploy.sh start

## 数据库设置

自增ID设置从最大值开始(以部门system_dept为例)：
SELECT setval('system_dept_id_seq', (SELECT MAX(id) FROM system_dept));

## 工程结构和开发约定
### 1. 后端工程结构
#### 通用模块
- gateway，统一网关。
- onebase-server，打包服务和统一工程配置项。
- dependencies ，统一管理所有依赖项目。
- framework，统一管理common公共能力和各类中间件。
#### 业务模块
- module-infra，基础设施模块，例如代码生成、API日志、配置管理、监控中心、文件管理、定时任务等。
- module-system，系统能力模块，例如租户管理、角色管理、菜单管理、部门管理、字段管理、审计日志等。
- module-bpm，工作流模块。
- module-data(计划新增)，元数据和数据管理模块。
- module-app(计划新增)，应用管理模块。
- module-app-resource(计划新增)，应用资源和协议管理。
- module-runtime(计划新增)，运行态支持，模块流量大，性能要求高。

### 2. 前端工程结构
- app-console，管理后台。
- app-builder，应用管理和页面编辑器。
- app-runtime(计划新增)，面向C端用户，运行态。

### 3. 包名约定
通用模块遵循现有方式；新建业务模块和moudle保持一致如system模块主包名：com.cmsr.onebase.module.system

### 4. 数据库建表约定
统一用模块名作为前缀，例如system模块的表均以system_为前缀，app均以app_为前缀。

### 5. Git 协作约定
以 dev/dev-20250721 为主干开发分支；
以 jira 任务task为出发点构建feature开发分支，命名：task_jiraID_创建日期。

**注意：**
- 多拉取，每天至少从主干分支至少拉取一次，避免冲突积累堆积。
- 严合并，必须以MR方式提交到主干分支，至少一位架构师点同意方可合并入主干。

### 6. AI 辅助编程的建议

1. 积极用AI，辅助编程。有条件上cursor 或 github copilot，没条件至少要用阿里的通义灵码。
2. 不盲信AI。由人理解和分析逻辑，由人来严格阅读和验证AI的代码，人来对代码的可读性、性能负责，不可以测通就直接无脑提交。