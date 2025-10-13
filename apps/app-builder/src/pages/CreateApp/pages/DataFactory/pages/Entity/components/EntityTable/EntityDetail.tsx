import { Tabs } from '@arco-design/web-react';
import React, { useState, useEffect } from 'react';
import styles from './EntityDetail.module.less';
import DataFields from './tabs/DataFields';
import DataMethods from './tabs/DataMethods';
import DataRules from './tabs/DataRules';
import Relations from './tabs/Relations';
import type { EntityNode, EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';

interface EntityDetailProps {
  entity: EntityNode;
  reloadList: () => void;
}

const EntityDetail: React.FC<EntityDetailProps> = ({ entity, reloadList }) => {
  const [activeTab, setActiveTab] = useState('fields');

  // 当实体变化时，重置到第一个标签页
  useEffect(() => {
    setActiveTab('fields');
  }, [entity.id]);

  const handleTabChange = (key: string) => {
    setActiveTab(key);
  };

  // 将EntityNode转换为EntityListItem
  const entityListItem: EntityListItem = {
    id: entity.id || entity.entityId,
    displayName: entity.displayName,
    code: entity.code || entity.entityName
  };

  return (
    <div className={styles.entityDetail}>
      <Tabs className={styles.tabs} activeTab={activeTab} onChange={handleTabChange}>
        <Tabs.TabPane key="fields" title="数据字段">
          <DataFields key={`fields-${entity.id || entity.entityId}`} entity={entityListItem} activeTab={activeTab} />
        </Tabs.TabPane>
        <Tabs.TabPane key="relations" title="关联关系">
          <Relations
            key={`relations-${entity.id || entity.entityId}`}
            entity={entityListItem}
            activeTab={activeTab}
            reloadList={reloadList}
          />
        </Tabs.TabPane>
        <Tabs.TabPane key="rules" title="数据规则">
          <DataRules key={`rules-${entity.id || entity.entityId}`} entity={entityListItem} activeTab={activeTab} />
        </Tabs.TabPane>
        <Tabs.TabPane key="methods" title="数据方法">
          <DataMethods key={`methods-${entity.id || entity.entityId}`} entity={entityListItem} activeTab={activeTab} />
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default EntityDetail;
