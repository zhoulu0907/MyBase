// 角色权限管理
export interface GetPermissionReq {
  /**
   * 应用Id
   */
  applicationId?: string;
  /**
   * 菜单Id
   */
  menuId?: string;
  /**
   * 角色Id
   */
  roleId?: string;
}

/**
 * 更新页面权限
 */
export interface UpdatePagePermissionReq {
  /**
   * 页面是否可访问
   */
  isPageAllowed: number;
  /**
   * 应用管理 - 权限基础参数
   */
  permissionReq: GetPermissionReq;
}


/**
 * 操作权限
 */
export interface UpdateOperationPermissionReq {
  /**
   * 操作权限
   */
  authOperation: AuthOperationVO;
  /**
   * 操作权限组
   */
  authOperations: AuthOperationVO[];
  /**
   * 应用管理 - 权限基础参数
   */
  permissionReq: GetPermissionReq;
}

export interface AuthOperationVO {
  /**
   * 操作名称
   */
  displayName: string;
  /**
   * 主键Id
   */
  id: string;
  /**
   * 是否允许
   */
  isAllowed: number;
  /**
   * 操作编码
   */
  operationCode: string;
}

/**
 * 更新数据组权限
 */
export interface UpdateDataGroupPermissionReq {
  /**
   * 数据访问
   */
  authDataGroup: AuthDataGroupVO;
  /**
   * 应用管理 - 权限基础参数
   */
  permissionReq: GetPermissionReq;
}

/**
 * 数据访问
 *
 * AuthDataGroupVO
 */
export interface AuthDataGroupVO {
  dataFilters?: Array<AuthDataFilterVO[]>;
  /**
   * 数据权限组描述
   */
  description?: string;
  /**
   * 数据权限组名称
   */
  groupName?: string;
  /**
   * 数据权限组排序
   */
  groupOrder?: number;
  /**
   * 主键Id
   */
  id?: string;
  /**
   * 是否可以操作
   */
  isOperable?: number;
  /**
   * 业务实体字段名称
   */
  scopeFieldId?: number;
  /**
   * 业务实体字段对应的权限范围
   */
  scopeLevel?: string;
}

/**
 * AuthDataFilterVO
 */
export interface AuthDataFilterVO {
  /**
   * 条件组
   */
  conditionGroup?: number;
  /**
   * 条件顺序
   */
  conditionOrder?: number;
  /**
   * 字段id
   */
  fieldId?: number;
  /**
   * 比较操作符号
   */
  fieldOperator?: string;
  /**
   * 字段值
   */
  fieldValue?: string;
  /**
   * 字段值类型
   */
  fieldValueType?: string;
  /**
   * 主键Id
   */
  id?: string;
}


/**
 * 更新字段权限
 */
export interface UpdateFieldPermissionReq {
  /**
   * 字段权限，当前下面情况必须传值：单个字段选择或不选择
   */
  authField?: AuthFieldVO;
  /**
   * 字段权限列表，当前下面情况必须传值：从全部到自定义
   */
  authFields?: AuthFieldVO[];
  /**
   * 所有字段可操作，当下面情况必须传值：从全部到自定义，或从自定义到全部
   */
  isAllFieldsAllowed?: number;
  /**
   * 应用管理 - 权限基础参数
   */
  permissionReq: GetPermissionReq;
}

/**
 * 字段权限，当前下面情况必须传值：单个字段选择或不选择
 *
 * AuthFieldVO
 */
export interface AuthFieldVO {
  /**
   * 字段名称
   */
  fieldDisplayName: string;
  /**
   * 字段id
   */
  fieldId: string;
  /**
   * 主键Id
   */
  id: string;
  /**
   * 是否可下载
   */
  isCanDownload: number;
  /**
   * 是否可编辑
   */
  isCanEdit: number;
  /**
   * 是否可阅读
   */
  isCanRead: number;
}


/**
 * 返回数据
 */
export interface FuncPermissionResponse {
  /**
   * 应用Id
   */
  applicationId: string;
  /**
   * 实体访问权限
   */
  authEntity: AuthDetailViewVO;
  /**
   * 操作权限
   */
  authOperations: AuthOperationVO[];
  /**
   * 页面是否可访问
   */
  isPageAllowed: number;
  /**
   * 菜单Id
   */
  menuId: number;
  /**
   * 角色Id
   */
  roleId: number;
}

/**
 * 实体访问权限
 *
 * AuthDetailViewVO
 */
export interface AuthDetailViewVO {
  /**
   * 实体访问权限
   */
  authViews?: AuthViewVO[];
  /**
   * 所有视图可访问
   */
  isAllViewsAllowed?: number;
}

/**
 * AuthViewVO
 */
export interface AuthViewVO {
  /**
   * 主键Id
   */
  id?: string;
  /**
   * 是否可访问
   */
  isAllowed?: number;
  /**
   * 实体名称
   */
  viewDisplayName?: string;
  /**
   * 实体id
   */
  viewId?: number;
}
