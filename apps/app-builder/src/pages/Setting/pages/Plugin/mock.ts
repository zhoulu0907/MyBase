
import type { PluginDetailRespVO, PluginVersionVO } from '@onebase/platform-center';

// 定义本地的 PluginItem 接口，避免循环依赖
export interface MockPluginItem {
  id: string;
  icon: string;
  name: string;
  remark: string;
  status: number;
  pluginId?: string;
  pluginVersion?: string;
  versionCount?: number;
  createTime?: string;
  updateTime?: string;
  isDynamic?: boolean;
}

export const mockDynamicPlugins: MockPluginItem[] = [
  {
    id: '1001',
    icon: 'approval',
    name: '审批中心插件',
    remark: '用于外部用户审批流程的动态插件，支持自定义审批流配置。',
    status: 1,
    pluginId: 'approval-center',
    pluginVersion: '1.0.0',
    versionCount: 3,
    createTime: '2025-01-01 10:00:00',
    updateTime: '2025-01-10 12:00:00',
    isDynamic: true
  },
  {
    id: '1002',
    icon: 'chart',
    name: '数据报表插件',
    remark: '提供数据看板与报表导出的动态插件，支持多种图表类型展示。',
    status: 0,
    pluginId: 'report-center',
    pluginVersion: '2.3.1',
    versionCount: 5,
    createTime: '2025-02-15 09:30:00',
    updateTime: '2025-03-01 18:20:00',
    isDynamic: true
  },
  {
    id: '1003',
    icon: 'message',
    name: '消息通知插件',
    remark: '集成多种消息通道（邮件、短信、钉钉）的通知服务插件。',
    status: 1,
    pluginId: 'message-center',
    pluginVersion: '1.2.0',
    versionCount: 2,
    createTime: '2025-03-05 14:00:00',
    updateTime: '2025-03-10 09:00:00',
    isDynamic: true
  }
];

const getTime = (dateStr: string) => new Date(dateStr).getTime();

export const mockPluginDetails: Record<string, PluginDetailRespVO> = {
  '1001': {
    id: 1001,
    pluginId: 'approval-center',
    pluginName: '审批中心插件',
    pluginIcon: 'approval',
    pluginDescription: '用于外部用户审批流程的动态插件，支持自定义审批流配置。包含请假、报销等常用模板。',
    status: 1,
    createTime: getTime('2025-01-01 10:00:00'),
    updateTime: getTime('2025-01-10 12:00:00')
  },
  '1002': {
    id: 1002,
    pluginId: 'report-center',
    pluginName: '数据报表插件',
    pluginIcon: 'chart',
    pluginDescription: '提供数据看板与报表导出的动态插件，支持多种图表类型展示。支持导出Excel、PDF格式。',
    status: 0,
    createTime: getTime('2025-02-15 09:30:00'),
    updateTime: getTime('2025-03-01 18:20:00')
  },
  '1003': {
    id: 1003,
    pluginId: 'message-center',
    pluginName: '消息通知插件',
    pluginIcon: 'message',
    pluginDescription: '集成多种消息通道（邮件、短信、钉钉）的通知服务插件。',
    status: 1,
    createTime: getTime('2025-03-05 14:00:00'),
    updateTime: getTime('2025-03-10 09:00:00')
  }
};

export const mockPluginVersions: Record<string, PluginVersionVO[]> = {
  'approval-center': [
    {
      id: 101,
      version: '1.0.0',
      status: 1,
      updateTime: getTime('2025-01-10 12:00:00'),
      description: '正式发布版本，包含基础审批功能。',
      pluginId: 'approval-center',
      createTime: getTime('2025-01-10 12:00:00')
    },
    {
      id: 102,
      version: '0.9.0',
      status: 0,
      updateTime: getTime('2024-12-20 10:00:00'),
      description: 'Beta测试版',
      pluginId: 'approval-center',
      createTime: getTime('2024-12-20 10:00:00')
    },
    {
      id: 103,
      version: '0.8.0',
      status: 0,
      updateTime: getTime('2024-11-15 09:00:00'),
      description: 'Alpha内测版',
      pluginId: 'approval-center',
      createTime: getTime('2024-11-15 09:00:00')
    }
  ],
  'report-center': [
    {
      id: 201,
      version: '2.3.1',
      status: 0,
      updateTime: getTime('2025-03-01 18:20:00'),
      description: '修复大数据量导出的性能问题',
      pluginId: 'report-center',
      createTime: getTime('2025-03-01 18:20:00')
    },
    {
      id: 202,
      version: '2.3.0',
      status: 1,
      updateTime: getTime('2025-02-28 10:00:00'),
      description: '新增漏斗图和雷达图支持',
      pluginId: 'report-center',
      createTime: getTime('2025-02-28 10:00:00')
    }
  ],
  'message-center': [
     {
      id: 301,
      version: '1.2.0',
      status: 1,
      updateTime: getTime('2025-03-10 09:00:00'),
      description: '增加飞书通知渠道',
      pluginId: 'message-center',
      createTime: getTime('2025-03-10 09:00:00')
    }
  ]
};
