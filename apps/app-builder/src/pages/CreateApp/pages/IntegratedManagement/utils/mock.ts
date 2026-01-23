import type { ConnectorItem } from '@onebase/app';

export const mockConnectors: ConnectorItem[] = [
  {
    id: 'http-1',
    name: 'HTTP',
    icon: '',
    category: 'HTTP',
    type: 'system_preset',
    fields: {
      serviceType: 'HTTP',
      version: '1.0.0',
      authType: '账号密码/TOKEN',
      instanceCount: 0
    },
    canEdit: false
  },
  {
    id: 'postgresql-1',
    name: 'PostgreSQL',
    icon: '',
    category: '数据库',
    type: 'system_preset',
    fields: {
      serviceType: '数据库',
      version: '1.0.0',
      authType: '账号密码',
      instanceCount: 0
    },
    canEdit: false
  },
  {
    id: 'dingtalk-1',
    name: '钉钉开放平台',
    icon: '',
    category: '三化连接器',
    type: 'custom',
    fields: {
      serviceType: 'SaaS',
      version: '1.0.0',
      authType: 'NONE',
      instanceCount: 0
    },
    canEdit: true
  },
  {
    id: 'wechat-1',
    name: '微信小程序',
    icon: '',
    category: '三化连接器',
    type: 'custom',
    fields: {
      serviceType: 'SaaS',
      version: '1.0.0',
      authType: 'NONE',
      instanceCount: 0
    },
    canEdit: true
  },
  {
    id: 'redis-1',
    name: 'Redis',
    icon: '',
    category: '三化连接器',
    type: 'custom',
    fields: {
      serviceType: 'SaaS',
      version: '1.0.0',
      authType: '账号密码',
      instanceCount: 0
    },
    canEdit: true
  }
];
