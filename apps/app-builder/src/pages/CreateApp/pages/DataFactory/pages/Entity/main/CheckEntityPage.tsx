import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Radio, Tag } from '@arco-design/web-react';
import { IconMindMapping, IconNav } from '@arco-design/web-react/icon';
import { getDatasourceList } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import EntityTable from '../components/EntityTable';
import styles from '../index.module.less';
import { EntityERContainer } from './EntityERContainer';

interface DatasourceRecord {
  id: number;
  datasourceName: string;
  code: string;
  datasourceType: string;
  description: string;
  runMode: number;
  appId: string;
  creator: string;
  createTime: string;
}
export const CheckEntityPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('ER');
  const [refreshEntityList, setRefreshEntityList] = useState(false);
  const [onlyUpdateNode, setOnlyUpdateNode] = useState(false);
  const [dsData, setDsData] = useState<DatasourceRecord | null>(null);
  const { curAppId } = useAppStore();
  const { setCurDataSourceId } = useResourceStore();

  const getAppResources = async () => {
    try {
      const params = {
        appId: curAppId
      };
      const res = await getDatasourceList(params);
      if (res?.length > 0) {
        const dataSource = res?.[0];
        setDsData(dataSource);
        // 将数据源ID存储到store中
        setCurDataSourceId(dataSource.id.toString());
        console.log('数据源ID已存储到store:', dataSource.id);
      } else {
        console.warn('getAppResources - 未获取到数据源列表');
      }
    } catch (error) {
      console.error('getAppResources - API调用失败:', error);
    }
  };

  useEffect(() => {
    if (curAppId) {
      getAppResources();
    }
  }, [curAppId]);

  return (
    <div className={styles['entity-page']}>
      <div className={styles['entity-page-header']}>
        <div className={styles['entity-page-header-left']}>
          <span className={styles['entity-page-header-left-name']}>数据源名称</span>
          <Tag className={styles['entity-page-header-left-tag']}>数据源编码：{dsData?.code}</Tag>
          <Tag className={styles['entity-page-header-left-tag']}>创建人：{dsData?.creator}</Tag>
          <Tag className={styles['entity-page-header-left-tag']}>创建时间：{dsData?.createTime}</Tag>
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
