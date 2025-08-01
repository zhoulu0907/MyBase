import React, { useState, useEffect } from 'react';
import { Button, Tag, Radio } from '@arco-design/web-react';
import { IconPlus, IconNav, IconMindMapping } from '@arco-design/web-react/icon';
import EntityERWithModeSwitch from '../../components/EntityERExample';
import EntityTable from '../../components/EntityTable';
import styles from './index.module.less';

const CheckEntityPage: React.FC<{ handlePageType: (tab: string) => void }> = ({ handlePageType }) => {
  const [activeTab, setActiveTab] = useState('ER');

  useEffect(() => {
  }, []);


  return (
    <div className={styles['entity-page']}>
      <div className={styles['entity-page-header']}>
        <div className={styles['entity-page-header-left']}>
          <span className={styles['entity-page-header-left-name']}>数据源名称</span>
          <Tag className={styles['entity-page-header-left-tag']}>数据源编码：</Tag>
          <Tag className={styles['entity-page-header-left-tag']}>创建人：</Tag>
          <Tag className={styles['entity-page-header-left-tag']}>创建时间：</Tag>
        </div>

        <div className={styles['entity-page-header-right']}>
          <Button type='primary' onClick={() => {
            handlePageType('create-entity');
          }}>
            <IconPlus />
            创建业务实体
          </Button>
          <Radio.Group type='button' defaultValue='ER' style={{ marginLeft: 10 }} onChange={(value) => {
            setActiveTab(value);
          }}>
            <Radio value='ER'><IconMindMapping /></Radio>
            <Radio value='table'><IconNav /></Radio>
          </Radio.Group>
        </div>
      </div>

      { activeTab === 'ER' && (
          <div className={styles['entity-page-content']}><EntityERWithModeSwitch /></div>
      )}
      { activeTab === 'table' && (
          <div className={styles['entity-page-content']}><EntityTable /></div>
      )}
    </div>
  );
};

export default CheckEntityPage;
