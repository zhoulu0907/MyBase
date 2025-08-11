import { Radio, Tag } from '@arco-design/web-react';
import { IconMindMapping, IconNav } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import EntityTable from '../components/EntityTable';
import styles from '../index.module.less';
import { EntityERContainer } from './EntityERContainer';

export const CheckEntityPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('ER');
  const [refreshEntityList, setRefreshEntityList] = useState(false);
  const [onlyUpdateNode, setOnlyUpdateNode] = useState(false);
  const dsData = {
    name: '数据源',
    code: 'ds_1',
    creator: 'admin',
    createTime: '2025-01-01'
  };

  return (
    <div className={styles['entity-page']}>
      <div className={styles['entity-page-header']}>
        <div className={styles['entity-page-header-left']}>
          <span className={styles['entity-page-header-left-name']}>数据源名称</span>
          <Tag className={styles['entity-page-header-left-tag']}>数据源编码：{dsData.code}</Tag>
          <Tag className={styles['entity-page-header-left-tag']}>创建人：{dsData.creator}</Tag>
          <Tag className={styles['entity-page-header-left-tag']}>创建时间：{dsData.createTime}</Tag>
        </div>

        <div className={styles['entity-page-header-right']}>
          <Radio.Group
            type="button"
            defaultValue="ER"
            style={{ marginLeft: 10 }}
            onChange={(value) => {
              setActiveTab(value);
            }}
          >
            <Radio value="ER">
              <IconMindMapping />
            </Radio>
            <Radio value="table">
              <IconNav />
            </Radio>
          </Radio.Group>
        </div>
      </div>

      {activeTab === 'ER' && (
        <div className={styles['entity-page-content']}>
          <EntityERContainer
            refreshEntityList={refreshEntityList}
            setRefreshEntityList={setRefreshEntityList}
            onlyUpdateNode={onlyUpdateNode}
            setOnlyUpdateNode={setOnlyUpdateNode}
          />
        </div>
      )}

      {activeTab === 'table' && (
        <div className={styles['entity-page-content']}>
          <EntityTable />
        </div>
      )}
    </div>
  );
};
