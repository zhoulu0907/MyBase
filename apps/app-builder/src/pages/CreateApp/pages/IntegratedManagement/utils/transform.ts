import type { ConnectFlowNode, ConnectorItem } from '@onebase/app';
import jsNodeIcon from '@/assets/flow/connect/js_node.svg';

export const CATEGORY_MAP: Record<string, string> = {
  database: '数据库',
  http: 'HTTP',
  mq: '消息队列',
  saas: '三化连接器',
  search: '搜索引擎',
  storage: '对象存储'
};

const getIconByLevel1Code = (level1Code: string) => {
  if (level1Code === 'system_preset' && typeof window !== 'undefined') {
    return jsNodeIcon;
  }
  return '';
};

const getCategoryByLevel1Code = (level1Code: string): string => {
  return CATEGORY_MAP[level1Code] || level1Code;
};

const getAuthTypeLabel = (authType?: string): string => {
  if (!authType) return '账号密码';
  if (authType === 'NONE') return '无需认证';
  return authType;
};

/**
 * 根据 level1Code 获取连接器类型显示名称
 * @param level1Code 连接器类型一级分类代码
 * @returns 连接器类型显示名称
 */
export const getConnectorTypeLabel = (level1Code?: string): string => {
  if (!level1Code) return '未知类型';

  const typeMap: Record<string, string> = {
    script: 'JavaScript脚本',
    system_preset: '系统预设',
    custom: '自定义'
  };

  return typeMap[level1Code] || level1Code;
};

export const transformConnectorData = (
  raw: ConnectFlowNode[]
): ConnectorItem[] => {
  if (!Array.isArray(raw)) {
    console.error('transformConnectorData: 输入数据不是数组', raw);
    return [];
  }

  return raw.map((item, index) => {
    const level1Code = item.level1Code || 'system_preset';
    // 优先使用 nodeName，如果没有则使用 typeName，最后使用默认值
    const displayName = item.nodeName || item.typeName || '未命名连接器';
    return {
      id: item.typeCode || `node-${index}`,
      name: displayName,
      icon: getIconByLevel1Code(level1Code),
      category: getCategoryByLevel1Code(level1Code),
      type: level1Code === 'custom' ? 'custom' : 'system_preset',
      fields: {
        serviceType: getCategoryByLevel1Code(level1Code),
        version: '1.0.0',
        authType: getAuthTypeLabel('账号密码/TOKEN'),
        instanceCount: 0
      },
      canEdit: level1Code === 'custom'
    };
  });
};
