import React from 'react';
import DictManager from '@/pages/Setting/pages/SystemDict/components/dict-manager-component';
import type { DictManagerConfig } from '@/pages/Setting/pages/SystemDict/components/dict-manager-component';
import styles from './index.module.less';

const DataDictPage: React.FC = () => {
  // 配置带tabs的字典管理器
  const config: DictManagerConfig = {
    ui: {
      title: '数据字典管理',
      emptyText: '暂无字典数据',
      dictSearchPlaceholder: '搜索数据字典',
      dictDataSearchPlaceholder: '搜索字典值',
      addDictButtonText: '新建字典',
      addDictDataButtonText: '添加字典值'
    },
    tabs: {
      enabled: true,
      defaultTabKey: 'app',
      customDictTab: {
        key: 'app',
        title: '自定义字典'
        // 自定义字典的API和权限配置（后续可以添加）
        // api: {
        //   getDictList: customGetDictList,
        //   getDictDataList: customGetDictDataList,
        //   // ... 其他自定义API
        // },
        // permissions: {
        //   create: 'CUSTOM_CREATE_PERMISSION',
        //   update: 'CUSTOM_UPDATE_PERMISSION',
        //   delete: 'CUSTOM_DELETE_PERMISSION',
        //   query: 'CUSTOM_QUERY_PERMISSION',
        //   status: 'CUSTOM_STATUS_PERMISSION'
        // }
      },
      systemDictTab: {
        key: 'tenant',
        title: '系统字典'
        // 系统字典使用默认的API和权限
      }
    },
    isHideTenantAddDictButton: true
  };

  const handleDictChange = (dict: any) => {
    console.log('当前选中的字典:', dict);
    // 可以在这里处理字典变化逻辑
  };

  const handleDictDataChange = (data: any[]) => {
    console.log('字典数据变化:', data);
    // 可以在这里处理字典数据变化逻辑
  };

  return (
    <div className={styles.dictPage}>
      <DictManager config={config} onDictChange={handleDictChange} onDictDataChange={handleDictDataChange} />
    </div>
  );
};

export default DataDictPage;
