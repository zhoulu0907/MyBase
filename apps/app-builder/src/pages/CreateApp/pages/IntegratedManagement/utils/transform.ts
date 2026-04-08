import type { ConnectFlowNode, ConnectorItem } from '@onebase/app';
import httpNodeIcon from '@/assets/flow/connect/http_node.png';
import jsNodeIcon from '@/assets/flow/connect/js_node.png';

export const CATEGORY_MAP: Record<string, string> = {
  database: '数据库',
  http: 'HTTP',
  mq: '消息队列',
  saas: '三化连接器',
  search: '搜索引擎',
  storage: '对象存储'
};

const getCategoryByLevel1Code = (level1Code: string): string => {
  return CATEGORY_MAP[level1Code] || level1Code;
};

/**
 * 根据连接器类型判断是否为 HTTP 类型
 */
const isHttpConnector = (item: ConnectFlowNode): boolean => {
  const { level1Code, typeCode, nodeCode } = item;
  return level1Code === 'http' ||
         typeCode?.toUpperCase() === 'HTTP' ||
         nodeCode?.toUpperCase().includes('HTTP') ||
         false;
};

/**
 * 根据连接器类型判断是否为 JS/脚本类型
 */
const isScriptConnector = (item: ConnectFlowNode): boolean => {
  const { level1Code, typeCode, nodeCode } = item;
  return level1Code === 'script' ||
         typeCode?.toUpperCase() === 'SCRIPT' ||
         nodeCode?.toUpperCase().includes('SCRIPT') ||
         nodeCode?.toUpperCase().includes('JS') ||
         false;
};

/**
 * 根据连接器类型判断是否为泛微类型
 */
const isWeaverConnector = (item: ConnectFlowNode): boolean => {
  const { nodeCode, nodeName } = item;
  return nodeCode?.toLowerCase().includes('weaver') || nodeName?.includes('泛微') || false;
};

/**
 * 根据连接器信息获取对应的图标
 * @param item 连接器节点数据
 * @returns 图标路径，泛微连接器返回 null（隐藏图标）
 */
const getConnectorIcon = (item: ConnectFlowNode): string | null => {
  // 泛微连接器：隐藏图标
  if (isWeaverConnector(item)) {
    return null;
  }

  // HTTP 连接器
  if (isHttpConnector(item)) {
    return httpNodeIcon;
  }

  // JS/脚本连接器
  if (isScriptConnector(item)) {
    return jsNodeIcon;
  }

  // 其他连接器：返回 null（不显示图标）
  return null;
};

/**
 * 根据连接器类型获取认证方式
 */
const getAuthTypeByConnector = (item: ConnectFlowNode): string => {
  if (isHttpConnector(item)) {
    return 'API Key';
  }
  if (isScriptConnector(item)) {
    return '无需认证';
  }
  if (isWeaverConnector(item)) {
    return 'Token';
  }

  const { level1Code } = item;
  switch (level1Code) {
    case 'database':
      return '账号密码';
    case 'mq':
      return '账号密码';
    case 'search':
      return 'API Key';
    case 'storage':
      return 'Access Key';
    case 'saas':
      return 'OAuth 2.0';
    default:
      return '无需认证';
  }
};

/**
 * 根据连接器类型获取默认参数
 */
const getDefaultParamsByConnector = (item: ConnectFlowNode): string => {
  if (isHttpConnector(item)) {
    return '请求方式: GET/POST';
  }
  if (isScriptConnector(item)) {
    return '运行环境: Node.js';
  }
  if (isWeaverConnector(item)) {
    return '服务地址: E9';
  }

  const { level1Code } = item;
  switch (level1Code) {
    case 'database':
      return '端口: 3306';
    case 'mq':
      return '协议: AMQP';
    case 'search':
      return '索引: 默认索引';
    case 'storage':
      return '存储桶: 默认桶';
    case 'saas':
      return '服务: SaaS';
    default:
      return '-';
  }
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

  return raw
    .filter(item => !isWeaverConnector(item)) // 过滤掉泛微连接器
    .map((item, index) => {
      const level1Code = item.level1Code || 'system_preset';
      // 优先使用 nodeName，如果没有则使用 typeName，最后使用默认值
      const displayName = item.nodeName || item.typeName || '未命名连接器';
      // 优先使用 nodeCode（如 weaverE9、MQ_RABBITMQ_CONSUMER）
      const nodeId = item.nodeCode || item.typeCode || `node-${index}`;

      return {
        id: nodeId,
        name: displayName,
        icon: getConnectorIcon(item),
        category: getCategoryByLevel1Code(level1Code),
        type: level1Code === 'custom' ? 'custom' : 'system_preset',
        fields: {
          serviceType: getCategoryByLevel1Code(level1Code),
          version: '1.0.0',
          authType: getAuthTypeByConnector(item),
          defaultParams: getDefaultParamsByConnector(item),
          instanceCount: Math.floor(Math.random() * 10) // Mock count for demo
        },
        canEdit: level1Code === 'custom'
      };
    });
};