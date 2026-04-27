// 数据源配置类型
export interface DatasourceConfig {
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  url: string;
}

// 数据源保存请求
export interface DatasourceSaveReqVO {
  id?: number;
  datasourceName: string;
  code: string;
  datasourceType: string;
  config: DatasourceConfig;
  description?: string;
  runMode?: number;
  applicationId: string;
  lockVersion?: number;
  datasourceOrigin?: number;
}

// 数据源测试连接请求
export interface DatasourceTestConnectionReqVO {
  datasourceType: string;
  config: DatasourceConfig;
}

// 查询表名列表参数
export interface GetTablesParams {
  datasourceId: number;
  schemaName?: string;
  keyword?: string;
}

// 查询字段信息参数
export interface GetColumnsParams {
  datasourceId: number;
  tableName: string;
  schemaName?: string;
}

// 数据源分页查询参数
export interface GetDatasourcePageParams {
  pageNo: number;
  pageSize: number;
  datasourceName?: string;
  code?: string;
  datasourceType?: string;
  runMode?: number;
  applicationId: string;
}
