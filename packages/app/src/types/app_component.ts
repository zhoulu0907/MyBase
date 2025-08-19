
/**
 * 组件配置接口
 */
export interface ComponentConfig {
    /** 组件编码 */
    componentCode: string;
    /** 组件类型 */
    componentType: string;
    /** 配置信息（JSON 字符串） */
    config: string;
    /** 编辑数据（JSON 字符串） */
    editData: string;
    /** 父组件编码 */
    parentCode: string;
    /** 块索引 */
    blockIndex: number;
    /** 容器索引 */
    containerIndex: number;
  }