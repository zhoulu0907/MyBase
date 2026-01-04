/**
 * 操作权限接口定义
 */
export interface OperationPermission {
  pageAllowed: boolean;
  allViewsAllowed: boolean;
  allFieldsAllowed: boolean;
  canCreate: boolean;
  canEdit: boolean;
  canDelete: boolean;
  canImport: boolean;
  canExport: boolean;
  canShare: boolean;
}

/**
 * 字段权限接口定义
 */
export interface FieldPermission {
  allAllowed: boolean;
  allDenied: boolean;
  fields: string[];
}

/**
 * 菜单权限数据接口定义
 */
export interface MenuPermission {
  operationPermission: OperationPermission;
  fieldPermission: FieldPermission;
  viewUuids: string[];
}
