
import { Tabs } from '@arco-design/web-react';
import React, { useState } from 'react';
import styles from './EntityDetail.module.less';
import DataFields from './tabs/DataFields';
import DataMethods from './tabs/DataMethods';
import DataRules from './tabs/DataRules';
import Relations from './tabs/Relations';
interface Entity {
  id: string;
  displayName: string;
  code: string;
  fields: any[];
  relations: any[];
  rules: any[];
  methods: any[];
}


interface EntityDetailProps {
  entity: Entity;
}

const EntityDetail: React.FC<EntityDetailProps> = ({ entity }) => {
  const [activeTab, setActiveTab] = useState('fields');

  // useEffect(() => {
  //   setActiveTab('fields');
  // }, [entity]);

  const handleTabChange = (key: string) => {
    setActiveTab(key);
  };

  return (
    <div className={styles.entityDetail}>
      <Tabs className={styles.tabs} activeTab={activeTab} onChange={handleTabChange}>
        <Tabs.TabPane key="fields" title="数据字段">
          <DataFields entity={entity} activeTab={activeTab} />
        </Tabs.TabPane>
        <Tabs.TabPane key="relations" title="关联关系">
          <Relations entity={entity} activeTab={activeTab} />
        </Tabs.TabPane>
        <Tabs.TabPane key="rules" title="数据规则">
          <DataRules entity={entity} activeTab={activeTab} />
        </Tabs.TabPane>
        <Tabs.TabPane key="methods" title="数据方法">
          <DataMethods entity={entity} activeTab={activeTab} />
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default EntityDetail;
