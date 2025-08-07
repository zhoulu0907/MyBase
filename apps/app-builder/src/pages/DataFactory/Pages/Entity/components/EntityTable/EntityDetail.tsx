import React from 'react';
import { Tabs } from '@arco-design/web-react';
import type { EntityNode } from '../../../../utils/interface';
import DataFields from './tabs/DataFields';
import Relations from './tabs/Relations';
import DataRules from './tabs/DataRules';
import DataMethods from './tabs/DataMethods';
import styles from './EntityDetail.module.less';

interface EntityDetailProps {
  entity: EntityNode;
}

const EntityDetail: React.FC<EntityDetailProps> = ({ entity }) => {
  return (
    <div className={styles.entityDetail}>
      <div className={styles.header}>
        <h2>{entity.title}</h2>
        <div className={styles.entityInfo}>
          <span>ID: {entity.id}</span>
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
