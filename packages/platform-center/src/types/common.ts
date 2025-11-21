// 通用分页参数
export interface PageParam {
  pageNo?: number;
  pageSize?: number;
  [key: string]: any;
}

// 通用分页响应
export interface PageResult<T> {
  list: T[];
  total: number;
  pageNo?: number;
  pageSize?: number;
}

/**
 * 启用-1，禁用-0, 过期 -2
 */
export enum StatusEnum {
  DISABLE = 0,
  ENABLE = 1,
  EXPIRED = 2
}

export enum CodeType {
  CORP = 'corp',
  TENANT = 'tenant'
}
