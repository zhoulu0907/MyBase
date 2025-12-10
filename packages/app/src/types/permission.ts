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
   * 已选的操作权限
   */
  operationTags: string[];
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
 * 视图权限
 */
export interface UpdateViewPermissionReq {
    /**
     * 要更新的视图权限列表
     */
    authViews?: AuthViewVO[];
    /**
     * 所有字段可操作，当下面情况必须传值：从全部到自定义，或从自定义到全部
     */
    isAllViewsAllowed?: number;
    /**
     * 应用管理 - 权限基础参数
     */
    permissionReq: GetPermissionReq;
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
   * 权限范围
   */
  scopeTags?: string[];
  /**
   * 数据权限组排序
   */
  groupOrder?: number;
  /**
   * 主键Id
   */
  id?: string;
  /**
   * 页面主业务实体id
   */
  entityId?: string;
  /**
   * 页面主业务实体名称
   */
  entityName?: string;
  /**
   * 是否可以操作
   */
  operationTags?: string[];
  /**
   * 权限范围字段名称
   */
  scopeFieldName?: string;
  /**
   * 权限范围字段对应的id
   */
  scopeFieldUuid?: number;
  /**
   * 业务实体字段对应的权限范围
   */
  scopeLevel?: ScopeType;
  /**
   * 业务实体字段对应的权限范围值
   */
  scopeValue?: string;
}

export enum IsOperable {
  notAllowed,
  allowed = 1
}

export type ScopeType = 
  | 'self'
  | 'selfAndSubordinates'
  | 'mainDepartment'
  | 'mainDepartmentAndSubs'
  | 'specifiedDepartment'
  | 'specifiedPerson'
  | 'identityInfo'
  | 'all';

/**
 * 权限范围 可分配的权限范围
 */
export interface ScopeTypeOption {
  label: string;
  value: ScopeType;
}

/**
 * 数据权限范围人员字段 AuthDataFilterPersonVO
 */
export interface AuthDataPermissionPersonVO {
  /**
   * 主键id
   */
  PersonId?: string;
  /**
   * 操作人员字段名称
   */
  fieldName?: string;
  /**
   * 操作人员名称
   */
  displayName?: string;
  /**
   * entityId
   */
  entityId?: string;
  /**
   * Id
   */
  id?: string;
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
  fieldId?: string;
  /**
   * 字段名称
   */
  fieldName?: string;
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
 * 可选校验类型列表
 */
export interface FilterFieldCheckType {
  /**
     * 校验类型编码
     */
    code?: string;
    /**
     * 校验类型描述
     */
    description?: string;
    /**
     * 校验类型名称
     */
    name?: string;
    /**
     * 排序
     */
    sortOrder?: number;
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
   * 字段类型
   */
  fieldType: string;
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
   * 操作权限
   */
  authOperationTags: string[];
  /**
   * 页面是否可访问
   */
  isPageAllowed: number;
  /**
   * 实体访问权限
   */
  authViewVO: AuthDetailViewVO;
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
  viewUuid?: number;
}

/**
 * 枚举 页面访问权限
 * @enum
 */
export enum FunPermissionViewVisit {
  notVisit,
  canVisit
}

/**
 * 枚举 操作权限
 * @enum
 */
export enum FunOperationPermission {
  notOperateAllowed,
  canOperateAllowed
}

/**
 * 枚举 视图权限
 * @enum
 */
export enum FunViewPermission {
  ViewCustomFieldPermission,
  AllViewVisitAllowed
}

/**
 * 枚举 视图自定义权限
 * @enum
 */
export enum FunViewCustomPermission {
  notViewAllowed,
  canViewAllowed
}

/**
 * 枚举 数据操作权限
 * @enum
 */
export enum DataOperationEnum {
  /**
   * 查看
   */
  examine = 'examine',
  /**
   * 操作
   */
  operate = 'operate',
}

/**
 * 枚举 字段操作权限
 * @enum 
 */
export enum FieldValueType {
  /**
   * 静态值
   */
  static = 'static',
  /**
   * 变量
   */
  variable = 'variable',
}


/**
 * 枚举 字段权限
 * @enum
 */
export enum RoleAllFieldPermission {
  FieldCustomFieldPermission,
  AllFieldPermissionAllow
}

/**
 * 枚举 字段阅读权限
 * @enum
 */
export enum FieldRead {
  notRead,
  canRead
}

/**
 * 枚举 字段编辑权限
 * @enum
 */
export enum FieldEdit {
  notEdit,
  canEdit
}

/**
 * 枚举 字段下载权限
 * @enum
 */
export enum FieldDownloadable {
  notDownloadable,
  canDownloadable
}

/**
 * 枚举 字段可见性
 * @enum
 */
export enum Visibility {
  visible,
  hidden
}