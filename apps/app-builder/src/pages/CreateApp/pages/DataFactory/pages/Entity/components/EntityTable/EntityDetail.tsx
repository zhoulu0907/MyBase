import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Tabs } from '@arco-design/web-react';
import React from 'react';
import styles from './EntityDetail.module.less';
import DataFields from './tabs/DataFields';
import DataMethods from './tabs/DataMethods';
import DataRules from './tabs/DataRules';
import Relations from './tabs/Relations';

interface EntityDetailProps {
  entity: EntityNode;
}

const EntityDetail: React.FC<EntityDetailProps> = ({ entity }) => {
  return (
    <div className={styles.entityDetail}>
      <div className={styles.header}>
        <h2>{entity.entityName}</h2>
        <div className={styles.entityInfo}>
          <span>ID: {entity.entityId}</span>
          <span>字段数: {entity.fields?.length || 0}</span>
        </div>
      </div>

      <Tabs className={styles.tabs}>
        <Tabs.TabPane key="fields" title="数据字段">
          <DataFields entity={entity} />
        </Tabs.TabPane>
        <Tabs.TabPane key="relations" title="关联关系">
          <Relations entity={entity} />
        </Tabs.TabPane>
        <Tabs.TabPane key="rules" title="数据规则">
          <DataRules entity={entity} />
        </Tabs.TabPane>
        <Tabs.TabPane key="methods" title="数据方法">
          <DataMethods entity={entity} />
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default EntityDetail;
