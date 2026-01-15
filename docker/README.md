# 观前提示

运行docker-compose前，请确保已经按照`dockerfiles`中的`README`打包并上传镜像。

# 安装前准备

## 下载依赖及安装包

- dockerfiles中打包的镜像
- openjdk-17.0.2
- dolphinscheduler3.3.1的plugins依赖

整体目录结构如下:
```path
.
├── docker-compose.yaml
├── dolphinscheduler
│   ├── conf
│   │   └── standalone.yaml
│   ├── logs
│   └── plugins
├── jdk-17.0.2
├── onebase
│   ├── conf
│   │   ├── build.yaml
│   │   ├── flink.yaml
│   │   └── runtime.yaml
│   └── logs
│       └── archive
└── onebase.env
```

## 小海豚初始化

初始化前，请确保小海豚的plugins依赖如下:
```path
.
├── alert-plugins
│   ├── dolphinscheduler-alert-email-3.3.1.jar
│   ├── dolphinscheduler-alert-http-3.3.1.jar
│   ├── dolphinscheduler-alert-prometheus-3.3.1.jar
│   └── dolphinscheduler-alert-script-3.3.1.jar
├── datasource-plugins
│   ├── dolphinscheduler-datasource-aliyunserverlessspark-3.3.1.jar
│   ├── dolphinscheduler-datasource-athena-3.3.1.jar
│   ├── dolphinscheduler-datasource-clickhouse-3.3.1.jar
│   ├── dolphinscheduler-datasource-dameng-3.3.1.jar
│   ├── dolphinscheduler-datasource-databend-3.3.1.jar
│   ├── dolphinscheduler-datasource-db2-3.3.1.jar
│   ├── dolphinscheduler-datasource-dolphindb-3.3.1.jar
│   ├── dolphinscheduler-datasource-doris-3.3.1.jar
│   ├── dolphinscheduler-datasource-hana-3.3.1.jar
│   ├── dolphinscheduler-datasource-k8s-3.3.1.jar
│   ├── dolphinscheduler-datasource-kyuubi-3.3.1.jar
│   ├── dolphinscheduler-datasource-mysql-3.3.1.jar
│   ├── dolphinscheduler-datasource-oceanbase-3.3.1.jar
│   ├── dolphinscheduler-datasource-oracle-3.3.1.jar
│   ├── dolphinscheduler-datasource-postgresql-3.3.1.jar
│   ├── dolphinscheduler-datasource-presto-3.3.1.jar
│   ├── dolphinscheduler-datasource-redshift-3.3.1.jar
│   ├── dolphinscheduler-datasource-sagemaker-3.3.1.jar
│   ├── dolphinscheduler-datasource-snowflake-3.3.1.jar
│   ├── dolphinscheduler-datasource-spark-3.3.1.jar
│   ├── dolphinscheduler-datasource-sqlserver-3.3.1.jar
│   ├── dolphinscheduler-datasource-ssh-3.3.1.jar
│   ├── dolphinscheduler-datasource-starrocks-3.3.1.jar
│   ├── dolphinscheduler-datasource-trino-3.3.1.jar
│   ├── dolphinscheduler-datasource-vertica-3.3.1.jar
│   ├── dolphinscheduler-datasource-zeppelin-3.3.1.jar
│   └── mssql-jdbc-13.2.0.jre8.jar
├── storage-plugins
│   ├── dolphinscheduler-storage-abs-3.3.1.jar
│   ├── dolphinscheduler-storage-gcs-3.3.1.jar
│   ├── dolphinscheduler-storage-hdfs-3.3.1.jar
│   ├── dolphinscheduler-storage-obs-3.3.1.jar
│   ├── dolphinscheduler-storage-oss-3.3.1.jar
│   ├── dolphinscheduler-storage-s3-3.3.1.jar
│   └── hbase-noop-htrace-4.1.1.jar
└── task-plugins
    ├── dolphinscheduler-task-aliyunserverlessspark-3.3.1.jar
    ├── dolphinscheduler-task-chunjun-3.3.1.jar
    ├── dolphinscheduler-task-datafactory-3.3.1.jar
    ├── dolphinscheduler-task-datasync-3.3.1.jar
    ├── dolphinscheduler-task-datax-3.3.1.jar
    ├── dolphinscheduler-task-dinky-3.3.1.jar
    ├── dolphinscheduler-task-dms-3.3.1.jar
    ├── dolphinscheduler-task-dvc-3.3.1.jar
    ├── dolphinscheduler-task-emr-3.3.1.jar
    ├── dolphinscheduler-task-flink-3.3.1.jar
    ├── dolphinscheduler-task-flink-stream-3.3.1.jar
    ├── dolphinscheduler-task-hivecli-3.3.1.jar
    ├── dolphinscheduler-task-http-3.3.1.jar
    ├── dolphinscheduler-task-java-3.3.1.jar
    ├── dolphinscheduler-task-jupyter-3.3.1.jar
    ├── dolphinscheduler-task-k8s-3.3.1.jar
    ├── dolphinscheduler-task-kubeflow-3.3.1.jar
    ├── dolphinscheduler-task-linkis-3.3.1.jar
    ├── dolphinscheduler-task-mlflow-3.3.1.jar
    ├── dolphinscheduler-task-mr-3.3.1.jar
    ├── dolphinscheduler-task-openmldb-3.3.1.jar
    ├── dolphinscheduler-task-procedure-3.3.1.jar
    ├── dolphinscheduler-task-python-3.3.1.jar
    ├── dolphinscheduler-task-pytorch-3.3.1.jar
    ├── dolphinscheduler-task-remoteshell-3.3.1.jar
    ├── dolphinscheduler-task-sagemaker-3.3.1.jar
    ├── dolphinscheduler-task-seatunnel-3.3.1.jar
    ├── dolphinscheduler-task-shell-3.3.1.jar
    ├── dolphinscheduler-task-spark-3.3.1.jar
    ├── dolphinscheduler-task-sql-3.3.1.jar
    ├── dolphinscheduler-task-sqoop-3.3.1.jar
    └── dolphinscheduler-task-zeppelin-3.3.1.jar
```

### 1. 初始化小海豚数据库

```bash
docker run --rm --entrypoint "/bin/sh tools/bin/upgrade-schema.sh" --name ds-init \
    --env-file onebase.env \
    apache/dolphinscheduler-tools:3.3.1
```

### 2. 手动初始化相关数据

0. 临时启动小海豚

```bash
docker-compose up -d onebase-ds-standalone
```

1. 登入小海豚界面

- 地址：`http://localhost:12345/dolphinscheduler`
- 默认用户名：`admin`
- 默认密码：`dolphinscheduler123`

2. 进入安全中心
    1. 创建租户`root`
    2. 创建令牌并保存令牌
    3. 创建环境并获取环境ID

```bash
export JAVA_HOME=/opt/jdk-17.0.2
export FLINK_HOME=/opt/flink
export PATH=$PATH:$JAVA_HOME/bin:$FLINK_HOME/bin
```

3. 进入项目管理
    1. 分别创建项目`自动化工作流`和`数据工厂`

4. 修改`onebase/conf/build.yaml`及`onebase/conf/runtime.yaml`中的相关配置

# 启动项目
```bash
docker-compose up -d
```

