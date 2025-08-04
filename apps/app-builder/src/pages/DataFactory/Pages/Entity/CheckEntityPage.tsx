import React, { useState } from 'react';
import { Button, Tag, Radio } from '@arco-design/web-react';
import { IconPlus, IconNav, IconMindMapping } from '@arco-design/web-react/icon';
import EntityTable from '../../components/EntityTable';
import styles from './index.module.less';
import EntityERWithModeSwitch from '../../components/EntityERExample';
import CreateEntityPage from './CreateEntityPage';

const CheckEntityPage: React.FC<{ handlePageType: (tab: string) => void }> = ({ handlePageType }) => {
  const [activeTab, setActiveTab] = useState('ER');
  const [createEntityModalVisible, setCreateEntityModalVisible] = useState(false);
  const [refreshEntityList, setRefreshEntityList] = useState(false);
  const dsData = {
    name: '数据源',
    code: 'ds_1',
    creator: 'admin',
    createTime: '2025-01-01',
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
          <Radio.Group type='button' defaultValue='ER' style={{ marginLeft: 10 }} onChange={(value) => {
            setActiveTab(value);
          }}>
            <Radio value='ER'><IconMindMapping /></Radio>
            <Radio value='table'><IconNav /></Radio>
          </Radio.Group>
        </div>
      </div>

      { activeTab === 'ER' && (
        <>
          <Button type='primary' className={styles['entity-page-create-button']} onClick={() => {
            setCreateEntityModalVisible(true);
          }}>
            <IconPlus />
            创建业务实体
          </Button>
          <div className={styles['entity-page-content']}><EntityERWithModeSwitch refreshEntityList={refreshEntityList} setRefreshEntityList={setRefreshEntityList} /></div>
        </>
      )}
      
      { activeTab === 'table' && (
          <div className={styles['entity-page-content']}><EntityTable /></div>
      )}

      <CreateEntityPage
        visible={createEntityModalVisible}
        setVisible={setCreateEntityModalVisible}
        handlePageType={handlePageType}
        successCallback={() => {
          setRefreshEntityList(true);
        }}
      />
    </div>
  );
};

export default CheckEntityPage;
